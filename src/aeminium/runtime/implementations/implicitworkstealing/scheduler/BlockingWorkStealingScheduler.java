/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 * 
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

package aeminium.runtime.implementations.implicitworkstealing.scheduler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.events.EventManager;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.stealing.WorkStealingAlgorithm;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitAtomicTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitBlockingTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitNonBlockingTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;
import aeminium.runtime.profiler.AeminiumProfiler;
import aeminium.runtime.profiler.DataCollection;

/* This scheduler works as a manager of all the tasks.
 * The scheduler decides to which thread a new task is sent.
 */
public final class BlockingWorkStealingScheduler {
	protected final ImplicitWorkStealingRuntime rt;
	protected ConcurrentLinkedQueue<WorkStealingThread> parkedThreads;
	protected WorkStealingThread[] threads;
	protected EventManager eventManager = null;
	protected AtomicInteger counter;
	protected Queue<ImplicitTask> submissionQueue;
	protected final int maxParallelism;
	protected WorkStealingAlgorithm wsa;
	protected BlockingThreadPool blockingThreadPool;
	protected static final boolean oneTaskPerLevel = Configuration.getProperty(BlockingWorkStealingScheduler.class, "oneTaskPerLevel", false);
	protected static final boolean useBlockingThreadPool = Configuration.getProperty(BlockingWorkStealingScheduler.class, "useBlockingThreadPool", false);
	protected static final int maxQueueLength = Configuration.getProperty(BlockingWorkStealingScheduler.class, "maxQueueLength", 0);
	protected static final int initialUnparkInterval = Configuration.getProperty(BlockingWorkStealingScheduler.class, "unparkInterval", 100);

	public static volatile int unparkInterval = initialUnparkInterval;
	protected static Boolean active_park = false;

	protected final boolean enableProfiler = Configuration.getProperty(getClass(), "enableProfiler", true);
	protected AeminiumProfiler profiler;

	/**************** Statistics******** */
	public int numberOfTasks = 0;
	public int[] numberOfTasksByWorker;
	public int[] numberOfStealsByWorker;
	public int[] numberOfNoStealsByWorker;
	public int[] totalByTypeId = new int[1000];
	public int[] numberOfTasksByQueue = new int[3];

	public BlockingWorkStealingScheduler(ImplicitWorkStealingRuntime rt) {
		this.rt = rt;
		maxParallelism = Configuration.getProcessorCount();
		// statistics
		numberOfTasksByWorker = new int[maxParallelism];
		numberOfStealsByWorker = new int[maxParallelism];
		numberOfNoStealsByWorker = new int[maxParallelism];
	}

	public BlockingWorkStealingScheduler(ImplicitWorkStealingRuntime rt, int maxParallelism) {
		this.rt = rt;
		this.maxParallelism = maxParallelism;
	}

	/*
	 * Initializes the scheduler, creating threads, queues and loads the
	 * WorkStealing algorith.
	 */
	public void init(EventManager eventManager) {
		this.eventManager = eventManager;
		this.parkedThreads = new ConcurrentLinkedQueue<WorkStealingThread>();
		this.threads = new WorkStealingThread[maxParallelism];
		this.counter = new AtomicInteger(threads.length);
		this.submissionQueue = new ConcurrentLinkedQueue<ImplicitTask>();
		this.wsa = loadWorkStealingAlgorithm(Configuration.getProperty(BlockingWorkStealingScheduler.class, "workStealingAlgorithm", "SequentialReverseScan"));
		if (useBlockingThreadPool) {
			blockingThreadPool = new BlockingThreadPool();
			blockingThreadPool.init(rt, eventManager);
		}

		// initialize data structures
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new WorkStealingThread(rt, i);
		}

		// setup WorkStealingAlgorithm
		wsa.init(threads, submissionQueue);

		// start and register threads threads
		for (WorkStealingThread thread : threads) {
			thread.start();
		}
	}

	/* Shutdowns all threads and releases all states. */
	public void shutdown() {
		// statistiscs print
		printStatistics();

		counter.set(threads.length);
		while (counter.get() > 0) {
			for (WorkStealingThread thread : threads) {
				thread.shutdown();
				LockSupport.unpark(thread);
			}
		}

		// cleanup
		wsa.shutdown();
		wsa = null;
		threads = null;
		parkedThreads = null;
		counter = null;
		submissionQueue = null;
		if (useBlockingThreadPool) {
			blockingThreadPool.shutdown();
		}
	}

	protected WorkStealingAlgorithm loadWorkStealingAlgorithm(String name) {
		WorkStealingAlgorithm wsa = null;

		Class<?> wsaClass = null;
		try {
			wsaClass = getClass().getClassLoader().loadClass("aeminium.runtime.implementations.implicitworkstealing.scheduler.stealing." + name);
		} catch (ClassNotFoundException e) {
			rt.getErrorManager().signalInternalError(new Error("Cannot load work stealing algorithm class : aeminium.runtime.implementations.implicitworkstealing.scheduler.stealing." + name));
		}

		try {
			wsa = (WorkStealingAlgorithm) wsaClass.newInstance();
		} catch (Exception e) {
			rt.getErrorManager().signalInternalError(new Error("Cannot load work stealing algorithm class : aeminium.runtime.implementations.implicitworkstealing.scheduler.stealing." + name));
			throw new Error("Cannot load work stealing algorithm class : aeminium.runtime.implementations.implicitworkstealing.scheduler.stealing." + name);
		}

		return wsa;
	}

	public final void registerThread(WorkStealingThread thread) {
		eventManager.signalNewThread(thread);
	}

	public final void unregisterThread(WorkStealingThread thread) {
		counter.decrementAndGet();
	}

	/* Receives a new task and forwards it to one of the executor threads. */
	public final void scheduleTask(ImplicitTask task) {
		if (task instanceof ImplicitBlockingTask && useBlockingThreadPool) {
			blockingThreadPool.submitTask((ImplicitBlockingTask) task);
			return;
		}
		if (maxQueueLength > 0) {
			Thread thread = Thread.currentThread();
			if (thread instanceof WorkStealingThread) {
				WorkStealingThread wthread = (WorkStealingThread) thread;
				WorkStealingQueue<ImplicitTask> taskQueue = wthread.getTaskQueue();
				if (taskQueue.size() < maxQueueLength || wthread.remainingRecursionDepth == 0) {
					taskQueue.push(task);
					if (taskQueue.size() <= 1) {
						signalWork();
					}
				} else {
					// statistics
					rt.scheduler.setStatistics(task, wthread.index, "executed");
					// **********

					wthread.remainingRecursionDepth--;
					task.invoke(rt);
					wthread.remainingRecursionDepth++;

					if (enableProfiler) {
						if (task instanceof ImplicitAtomicTask)
							wthread.incrementNoAtomicTasksHandled();
						else if (task instanceof ImplicitBlockingTask)
							wthread.incrementNoBlockingTasksHandled();
						else if (task instanceof ImplicitNonBlockingTask)
							wthread.incrementNoNonBlockingTasksHandled();
					}
				}
			} else {
				submissionQueue.add(task);
				signalWork();
			}
		} else {
			Thread thread = Thread.currentThread();
			if (thread instanceof WorkStealingThread) {
				// worker thread
				WorkStealingThread wthread = (WorkStealingThread) thread;
				if (oneTaskPerLevel) {
					WorkStealingQueue<ImplicitTask> taskQueue = wthread.getTaskQueue();
					ImplicitTask head = taskQueue.peek();
					if (head != null && head.level == task.level && wthread.remainingRecursionDepth > 0) {
						// statistics
						rt.scheduler.setStatistics(task, wthread.index, "executed");
						// **********

						wthread.remainingRecursionDepth--;
						task.invoke(rt);
						wthread.remainingRecursionDepth++;

						if (enableProfiler) {
							if (task instanceof ImplicitAtomicTask)
								wthread.incrementNoAtomicTasksHandled();
							else if (task instanceof ImplicitBlockingTask)
								wthread.incrementNoBlockingTasksHandled();
							else if (task instanceof ImplicitNonBlockingTask)
								wthread.incrementNoNonBlockingTasksHandled();
						}

					} else {
						taskQueue.push(task);
						if (taskQueue.size() <= 1) {
							signalWork(wthread);
						}
					}
				} else {
					wthread.getTaskQueue().push(task);
					if (wthread.getTaskQueue().size() <= 1) {
						signalWork(wthread);
					}
				}
			} else {
				// external thread
				submissionQueue.add(task);
				signalWork();
			}
		}
	}

	/* Awakes a specific thread. */
	public final void signalWork(WorkStealingThread thread) {
		// TODO: need to fix that to wake up thread waiting for objects to
		// complete
		LockSupport.unpark(thread);
		WorkStealingThread next = wsa.signalWorkInLocalQueue(thread);
		LockSupport.unpark(next);
	}

	/* Awakes a thread to perform some work. */
	public final void signalWork() {
		WorkStealingThread threadParked = wsa.signalWorkInSubmissionQueue();
		if (threadParked != null) {
			LockSupport.unpark(threadParked);
		}
	}

	/* Parks a thread to wait an interval before looking for new work. */
	public final void parkThread(WorkStealingThread thread) {
		eventManager.signalThreadSuspend(thread);
		wsa.threadGoingToPark(thread);

		boolean active;

		synchronized (active_park) {
			if (active_park)
				active = false;
			else {
				active = true;
				active_park = true;
			}
		}

		if (active) {
			LockSupport.parkNanos(thread, unparkInterval);
			unparkInterval *= 2;

			synchronized (active_park) {
				active_park = false;
			}
		} else
			LockSupport.park(thread);
	}

	/* Steals work from queues using an algorithm. */
	public final ImplicitTask scanQueues(WorkStealingThread thread) {
		return wsa.stealWork(thread);
	}

	/* Removes a task from que submission queue. */
	public boolean cancelTask(ImplicitTask task) {
		boolean result = submissionQueue.remove(task);
		if (result) {
			task.taskFinished(rt);
		}
		return result;
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * PROFILER * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * *
	 */
	public synchronized void collectData(DataCollection data) {

		if (threads == null)
			return;

		for (int i = 0; i < threads.length; i++) {
			data.taskInNonBlockingQueue[i] = threads[i].getLocalQueueSize();
			threads[i].getNoTasksHandled(data.tasksHandled[i]);

			if (blockingThreadPool != null)
				data.taskInBlockingQueue = blockingThreadPool.getTaskQueueSize();
		}

		return;
	}

	public int getMaxParallelism() {
		return maxParallelism;
	}

	public void setProfiler(AeminiumProfiler profiler) {
		this.profiler = profiler;

		for (int i = 0; i < threads.length; i++) {
			threads[i].setProfiler(this.profiler);
		}
	}

	/*********** Statistics ************/
	public void incrementNumberOfTasks() {
		numberOfTasks++;
	}

	public void incrementNumberOfTasksByWorker(int index) {
		numberOfTasksByWorker[index]++;
	}

	public void incrementNumberOfStealsByWorker(int index) {
		numberOfStealsByWorker[index]++;
	}

	public void incrementNumberOfNoStealsByWorker(int index) {
		numberOfNoStealsByWorker[index]++;
	}

	public void incrementTotalByTypeId(int typeId) {
		totalByTypeId[typeId]++;
	}

	public void incrementNumberOfTasksByQueue(String queueName) {
		// taskQueue
		if (queueName.compareTo("taskQueue") == 0) {
			numberOfTasksByQueue[0]++;
			// stealQueue
		} else if (queueName.compareTo("stealQueue") == 0) {
			numberOfTasksByQueue[1]++;
			// executed
		} else if (queueName.compareTo("executed") == 0) {
			numberOfTasksByQueue[2]++;
		}

	}

	private void printStatistics() {
		System.out.println("TOTAL TASKS PERFORMED BY WORKER:");
		for (int i = 0; i < maxParallelism; i++)
			System.out.print("workerId" + i + ": " + numberOfTasksByWorker[i] + " | ");
		System.out.println();

		System.out.println("TOTAL STEALS PERFORMED BY WORKER:");
		for (int i = 0; i < maxParallelism; i++)
			System.out.print("workerId" + i + ": " + numberOfStealsByWorker[i] + " | ");
		System.out.println();

		System.out.println("TOTAL OF NO STEALS PERFORMED BY WORKER:");
		for (int i = 0; i < maxParallelism; i++)
			System.out.print("workerId" + i + ": " + numberOfNoStealsByWorker[i] + " | ");
		System.out.println();

		System.out.println("TOTAL OF TASKS BY TYPE:");
		for (int i = 0; i < 1000; i++) {
			if (totalByTypeId[i] > 0) {
				System.out.print("typeId" + i + ": " + totalByTypeId[i] + " | ");
			}
		}
		System.out.println();

		System.out.println("TOTAL OF TASKS BY QUEUE:");
		System.out.print("taskQueue: " + numberOfTasksByQueue[0] + " | " + "stealQueue: " + numberOfTasksByQueue[1] + " | " + "executed: " + numberOfTasksByQueue[2]);

		System.out.println();

	}

	// Set the values to the statistics
	public void setStatistics(ImplicitTask task, int workerId, String queueName) {
		task.setWorkerId(workerId);
		task.setQueueName(queueName);
		rt.scheduler.incrementNumberOfTasksByWorker(workerId);
		rt.scheduler.incrementNumberOfNoStealsByWorker(workerId);
		rt.scheduler.incrementTotalByTypeId(task.typeId);
		rt.scheduler.incrementNumberOfTasksByQueue(queueName);
	}

}
