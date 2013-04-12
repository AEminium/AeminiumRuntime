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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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

	public int[] numberOfTasksByWorker;
	public int[] numberOfStealsByWorker;
	public int[] numberOfNoStealsByWorker;
	public int[] numberOfStealedTasksInWorker;
	public int[] numberOfTasksByQueue = new int[3];
	public int[] numberOfTaskInsertedByWorkerQueue;
	public int[] numberOfTaskInWorkerQueue;
	public int numberOfStealedTasksInSubmissionQueue = 0;
	public int MAX_TASK_TYPES = 10000;
	public int[] totalByTypeId = new int[MAX_TASK_TYPES];
	public ArrayList[] workerTaskId;
	public ArrayList[] workerTaskTypeId;
	public ArrayList[] taskIdDependentsByTypeId;
	public ArrayList[] taskTypeIdDependentsByTypeId;
	public long totalTimestampWaitingInQueue=0;

	public int positionThread = 0;

	public BlockingWorkStealingScheduler(ImplicitWorkStealingRuntime rt) {
		this.rt = rt;
		maxParallelism = Configuration.getProcessorCount();
		// statistics
		numberOfTasksByWorker = new int[maxParallelism];
		numberOfStealsByWorker = new int[maxParallelism];
		numberOfNoStealsByWorker = new int[maxParallelism];
		numberOfTaskInsertedByWorkerQueue = new int[maxParallelism];
		numberOfTaskInWorkerQueue = new int[maxParallelism];
		workerTaskId = new ArrayList[maxParallelism];
		for (int i = 0; i < maxParallelism; i++)
			workerTaskId[i] = new ArrayList<String>();
		workerTaskTypeId = new ArrayList[maxParallelism];
		for (int i = 0; i < maxParallelism; i++)
			workerTaskTypeId[i] = new ArrayList<String>();
		taskIdDependentsByTypeId = new ArrayList[MAX_TASK_TYPES];
		for (int i = 0; i < MAX_TASK_TYPES; i++)
			taskIdDependentsByTypeId[i] = new ArrayList<String>();
		taskTypeIdDependentsByTypeId = new ArrayList[MAX_TASK_TYPES];
		for (int i = 0; i < MAX_TASK_TYPES; i++)
			taskTypeIdDependentsByTypeId[i] = new ArrayList<String>();
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
		
		task.timestampEnterQueue = new Date().getTime();

		
		if (maxQueueLength > 0) {
			Thread thread = Thread.currentThread();
			if (thread instanceof WorkStealingThread) {
				WorkStealingThread wthread = (WorkStealingThread) thread;
				WorkStealingQueue<ImplicitTask> taskQueue = wthread.getTaskQueue();

				if (taskQueue.size() < maxQueueLength || wthread.remainingRecursionDepth == 0) {
					// statistics (increment the total tasks inserted in worker
					// queue)
					rt.scheduler.incrementTotalTasksInsertedInWorker(wthread.index);

					taskQueue.push(task);
					if (taskQueue.size() <= 1) {
						signalWork();
					}
				} else {
					// statistics (increment the total tasks inserted in worker
					// queue)
					rt.scheduler.incrementTotalTasksInsertedInWorker(wthread.index);
					// statistics (called when task.invoke)
					rt.scheduler.setStatistics(task, wthread.index, "executed");

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
			//Thread thread = Thread.currentThread();
			
			Thread thread =null;
			int i; 
			for (i = 0; i < maxParallelism; i++) {
				if (numberOfTaskInWorkerQueue[i] < 1) {
					thread = threads[i];
					break;
				}
			}
			if(thread==null){
				thread=Thread.currentThread();
			}

			
			
			/*
			Thread thread = parkedThreads.poll();
			if(thread==null){
				thread=Thread.currentThread();
			}
			*/

			if (thread instanceof WorkStealingThread) {
				
				// worker thread
				WorkStealingThread wthread = (WorkStealingThread) thread;
				
				//
				numberOfTaskInWorkerQueue[wthread.index]++;
				task.workerQueueId = wthread.index;
				
				if (oneTaskPerLevel) {
					WorkStealingQueue<ImplicitTask> taskQueue = wthread.getTaskQueue();
					ImplicitTask head = taskQueue.peek();
					if (head != null && head.level == task.level && wthread.remainingRecursionDepth > 0) {
						// statistics
						rt.scheduler.setStatistics(task, wthread.index, "executed");

						// statistics (increment the total tasks inserted in
						// worker queue)
						rt.scheduler.incrementTotalTasksInsertedInWorker(wthread.index);
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
						// statistics (increment the total tasks inserted in
						// worker queue)
						rt.scheduler.incrementTotalTasksInsertedInWorker(wthread.index);
						// ***********

						taskQueue.push(task);
						if (taskQueue.size() <= 1) {
							signalWork();
						}
					}
				} else {

					// statistics (increment the total tasks inserted in
					// worker
					// queue)
					rt.scheduler.incrementTotalTasksInsertedInWorker(wthread.index);
					// ***********

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

	private void incrementNumberOfTasksByWorker(int workerId) {
		numberOfTasksByWorker[workerId]++;
	}

	private void addTaskIdToWorker(int taskId, int workerId) {
		workerTaskId[workerId].add(taskId);
	}

	private void addTaskTypeIdToWorker(int taskTypeId, int workerId) {
		workerTaskTypeId[workerId].add(taskTypeId);
	}

	private void incrementNumberOfStealsByWorker(int index) {
		numberOfStealsByWorker[index]++;
	}

	private void incrementNumberOfStealedTasksInWorker(int index) {
		numberOfStealedTasksInWorker[index]++;
	}

	private void incrementNumberOfStealedTasksInSubmissionQueue() {
		numberOfStealedTasksInSubmissionQueue++;
	}

	private void incrementNumberOfNoStealsByWorker(int index) {
		numberOfNoStealsByWorker[index]++;
	}

	private void incrementTotalByTypeId(int typeId) {
		if (typeId >= 0)
			totalByTypeId[typeId]++;
		else
			System.out.println("typeId Invalid");
	}

	public void incrementTotalDependentsByTypeId(int typeId, ArrayList<String> dependentsId, ArrayList<String> dependentsTypeId) {
		if (typeId >= 0) {
			synchronized (this) {
				for (int i = 0; i < dependentsId.size(); i++)
					taskIdDependentsByTypeId[typeId].add(dependentsId.get(i));
				for (int i = 0; i < dependentsTypeId.size(); i++)
					taskTypeIdDependentsByTypeId[typeId].add(dependentsTypeId.get(i));
			}
		} else {
			System.out.println("typeId Invalid");
		}
	}

	private void incrementNumberOfTasksByQueue(String queueName) {
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

	public void printStatistics() {
		System.out.println("***************************************STATISTICS BY WORKER******************************");
		DecimalFormat df = new DecimalFormat("#.##");
		double totalTasks = 0;
		for (int i = 0; i < maxParallelism; i++)
			totalTasks += numberOfTasksByWorker[i];
		System.out.println("TOTAL TASKS PERFORMED: " + totalTasks);
		for (int i = 0; i < maxParallelism; i++)
			System.out.print("workerId" + i + ": " + numberOfTasksByWorker[i] + "(" + df.format((numberOfTasksByWorker[i] / totalTasks) * 100) + "%) | ");
		System.out.println();
		// *********
		totalTasks = 0;
		for (int i = 0; i < maxParallelism; i++)
			totalTasks += numberOfStealsByWorker[i];
		System.out.println("TOTAL STEALS PERFORMED BY WORKER: " + totalTasks);
		for (int i = 0; i < maxParallelism; i++)
			System.out.print("workerId" + i + ": " + numberOfStealsByWorker[i] + "(" + df.format((numberOfStealsByWorker[i] / totalTasks) * 100) + "%) | ");
		System.out.println();
		// **********
		totalTasks = 0;
		for (int i = 0; i < maxParallelism; i++)
			totalTasks += numberOfNoStealsByWorker[i];
		System.out.println("TOTAL OF NO STEALS PERFORMED BY WORKER: " + totalTasks);
		for (int i = 0; i < maxParallelism; i++)
			System.out.print("workerId" + i + ": " + numberOfNoStealsByWorker[i] + "(" + df.format((numberOfNoStealsByWorker[i] / totalTasks) * 100) + "%) | ");
		System.out.println();
		// **********
		System.out.println("TASK ID SEQUENCE BY WORKER: ");
		for (int w = 0; w < maxParallelism; w++) {
			System.out.print("workerId" + w + ": ");
			for (int i = 0; i < workerTaskId[w].size(); i++) {
				System.out.print(workerTaskId[w].get(i) + ",");
			}
			System.out.println();
		}
		// **********
		System.out.println("TASK TYPE ID SEQUENCE BY WORKER: ");
		for (int w = 0; w < maxParallelism; w++) {
			System.out.print("workerId" + w + ": ");
			for (int i = 0; i < workerTaskTypeId[w].size(); i++) {
				System.out.print(workerTaskTypeId[w].get(i) + ",");
			}
			System.out.println();
		}
		// **********
		totalTasks = 0;
		for (int i = 0; i < maxParallelism; i++)
			totalTasks += numberOfTaskInsertedByWorkerQueue[i];
		System.out.println("TOTAL OF TASKS INSERTED BY WORKER QUEUE: " + totalTasks);
		for (int w = 0; w < maxParallelism; w++) {
			System.out.println("workerId" + w + ": " + numberOfTaskInsertedByWorkerQueue[w] + "(" + df.format((numberOfTaskInsertedByWorkerQueue[w] / totalTasks) * 100) + "%)");
		}
		// **********
		System.out.println("***************************************STATISTICS BY TASK TYPE***************************");
		totalTasks = 0;
		for (int i = 0; i < MAX_TASK_TYPES; i++) {
			if (totalByTypeId[i] > 0) {
				totalTasks += totalByTypeId[i];
			}
		}
		System.out.println("TOTAL OF TASKS BY TYPE: " + totalTasks);
		for (int i = 0; i < MAX_TASK_TYPES; i++) {
			if (totalByTypeId[i] > 0) {
				System.out.print("typeId" + i + ": " + totalByTypeId[i] + "(" + df.format((totalByTypeId[i] / totalTasks) * 100) + "%) | ");
			}
		}
		System.out.println();
		// **********
		totalTasks = 0;
		for (int i = 0; i < MAX_TASK_TYPES; i++) {
			if (taskIdDependentsByTypeId[i].size() > 0) {
				totalTasks += taskIdDependentsByTypeId[i].size();
			}
		}
		System.out.println("TOTAL OF DEPENDENTS (id) BY TASKS TYPE: " + totalTasks);
		for (int i = 0; i < MAX_TASK_TYPES; i++) {
			if (taskIdDependentsByTypeId[i].size() > 0) {
				System.out.print("typeId" + i + ": " + taskIdDependentsByTypeId[i].size() + " dependents -> id ");
				for (int j = 0; j < taskIdDependentsByTypeId[i].size(); j++)
					System.out.print("[" + taskIdDependentsByTypeId[i].get(j) + "]");
				System.out.println("(" + df.format((taskIdDependentsByTypeId[i].size() / totalTasks) * 100) + "%) | ");
			}
		}
		System.out.println();
		// **********
		totalTasks = 0;
		for (int i = 0; i < MAX_TASK_TYPES; i++) {
			if (taskTypeIdDependentsByTypeId[i].size() > 0) {
				totalTasks += taskTypeIdDependentsByTypeId[i].size();
			}
		}
		System.out.println("TOTAL OF DEPENDENTS (typeId) BY TASKS TYPE: " + totalTasks);
		for (int i = 0; i < MAX_TASK_TYPES; i++) {
			if (taskTypeIdDependentsByTypeId[i].size() > 0) {
				System.out.print("typeId" + i + ": " + taskTypeIdDependentsByTypeId[i].size() + " dependents -> typeId ");
				for (int j = 0; j < taskTypeIdDependentsByTypeId[i].size(); j++)
					System.out.print("[" + taskTypeIdDependentsByTypeId[i].get(j) + "]");
				System.out.println("(" + df.format((taskTypeIdDependentsByTypeId[i].size() / totalTasks) * 100) + "%) | ");
			}
		}
		System.out.println();
		// **********
		System.out.println("***************************************STATISTICS BY QUEUE TYPE***************************");
		totalTasks = numberOfTasksByQueue[0] + numberOfTasksByQueue[1] + numberOfTasksByQueue[2];
		System.out.println("TOTAL OF TASKS BY QUEUE: " + totalTasks);
		System.out.print("taskQueue: " + numberOfTasksByQueue[0] + " | " + "stealQueue: " + numberOfTasksByQueue[1] + " | " + "executed: " + numberOfTasksByQueue[2]);
		System.out.println();
		// **********
		System.out.println("***************************************STATISTICS BY TASK STEALS IN QUEUES*****************");
		System.out.println("TOTAL OF TASKS STEALED IN: ");
		System.out.print("SubmissionQueue: " + numberOfStealedTasksInSubmissionQueue);
		System.out.println();
		// **********
		totalTasks = 0;
		for (int i = 0; i < maxParallelism; i++)
			totalTasks += numberOfTasksByWorker[i];
		System.out.println("***************************************STATISTICS BY TASK TIME*****************");
		System.out.println("TOTAL OF TIME TASKS WAIT IN QUEUE: "+totalTimestampWaitingInQueue);
		System.out.print("SubmissionQueue: " + numberOfStealedTasksInSubmissionQueue);
		System.out.println();
		

	}

	// Set the values to the statistics
	public void setStatistics(ImplicitTask task, int workerId, String queueName) {
		synchronized (this) {
			if (task.workerId >= 0)
				incrementNumberOfStealedTasksInWorker((int) task.workerId);
			else if (task.stealedFromSubmissionQueue)
				incrementNumberOfStealedTasksInSubmissionQueue();
			task.setWorkerId(workerId);
			task.setQueueName(queueName);
			incrementNumberOfTasksByWorker(workerId);
			addTaskIdToWorker(task.id, workerId);
			addTaskTypeIdToWorker(task.typeId, workerId);
			if (queueName.compareTo("taskQueue") == 0)
				incrementNumberOfNoStealsByWorker(workerId);
			else if (queueName.compareTo("executed") == 0)
				incrementNumberOfNoStealsByWorker(workerId);
			else if (queueName.compareTo("stealQueue") == 0)
				incrementNumberOfStealsByWorker(workerId);
			incrementTotalByTypeId(task.typeId);
			incrementNumberOfTasksByQueue(queueName);
		}
	}

	public void incrementTotalTasksInsertedInWorker(int index) {
		synchronized (this) {
			numberOfTaskInsertedByWorkerQueue[index]++;
		}
	}
	
	public void incrementTotalTimestampWaitingInQueue(long t) {
		synchronized (this) {
			totalTimestampWaitingInQueue+=t;
		}
	}
	
	
}
