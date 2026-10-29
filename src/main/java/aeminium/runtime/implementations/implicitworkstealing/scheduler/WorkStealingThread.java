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

import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitAtomicTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitBlockingTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitNonBlockingTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;
import aeminium.runtime.profiler.AeminiumProfiler;
import aeminium.runtime.profiler.DataCollection;

/*
 * Thread that is part of the threadpool that executes NonBlocking tasks
 * and Atomic Tasks.
 *
 * A thread has its own queue of tasks to executed and follows a work-stealing
 * technique. If a task finds its queue empty, it will try to get another task
 * from the scheduler.
 */
public final class WorkStealingThread extends AeminiumThread {
	protected final ImplicitWorkStealingRuntime rt;
	public final int index;
	protected volatile boolean shutdown = false;
	protected final int pollingCount   = Configuration.getProperty(getClass(), "pollingCount", 5);
	public int remainingRecursionDepth = Configuration.getProperty(getClass(), "maxRecursionDepth", 512);
	protected WorkStealingQueue<ImplicitTask> taskQueue;
	protected static final AtomicInteger IdGenerator = new AtomicInteger(0);
	protected int steals = 0;
	protected int nosteals = 0;
	protected int parks = 0;
	protected int maxQueueSize = 0;
	protected int tasks = 0;

	/* Profiler information. */
	private AtomicInteger noAtomicTasksHandled = new AtomicInteger(0);
	private AtomicInteger noBlockingTasksHandled = new AtomicInteger(0);
	private AtomicInteger noNonBlockingTasksHandled = new AtomicInteger(0);

	protected final boolean enableProfiler = Configuration.getProperty(getClass(), "enableProfiler", true);
	protected AeminiumProfiler profiler;

	public WorkStealingThread(ImplicitWorkStealingRuntime rt, int index) {
		this.rt           = rt;
		this.index        = index;
		taskQueue = new ConcurrentWorkStealingQueue<ImplicitTask>(13);
		setName("WorkerStealingThread-"+IdGenerator.incrementAndGet());
	}

	public final WorkStealingQueue<ImplicitTask> getTaskQueue() {
		return taskQueue;
	}

	public final void shutdown() {
		shutdown = true;
	}

	/* Main loop that executes tasks from queue, steals if empty,
	 * or parks until some task awakes it.
	 */
	@Override
	public final void run() {
		super.run();
		int pollCounter = pollingCount;
		rt.scheduler.registerThread(this);
		while (!shutdown) {
			ImplicitTask task = null;
			maxQueueSize = Math.max(maxQueueSize, taskQueue.size());
			task = taskQueue.pop();

			if ( task != null ) {
				task.invoke(rt);
				tasks++;

				if (enableProfiler) {
					if (task instanceof ImplicitAtomicTask)
						noAtomicTasksHandled.getAndIncrement();
					else if (task instanceof ImplicitBlockingTask)
						noBlockingTasksHandled.getAndIncrement();
					else if (task instanceof ImplicitNonBlockingTask)
						noNonBlockingTasksHandled.getAndIncrement();
				}


			} else {
				// scan for other queues
				task = rt.scheduler.scanQueues(this);
				if ( task != null ) {
					steals++;
					task.invoke(rt);
					tasks++;

					rt.scheduler.signalWork();
					BlockingWorkStealingScheduler.unparkInterval = Math.max
					(
						BlockingWorkStealingScheduler.unparkInterval / 4,
						BlockingWorkStealingScheduler.initialUnparkInterval
					);

					if (enableProfiler) {
						if (task instanceof ImplicitAtomicTask)
							noAtomicTasksHandled.getAndIncrement();
						else if (task instanceof ImplicitBlockingTask)
							noBlockingTasksHandled.getAndIncrement();
						else if (task instanceof ImplicitNonBlockingTask)
							noNonBlockingTasksHandled.getAndIncrement();
					}

				} else {
					nosteals++;
					if ( pollCounter == 0) {
						parks++;
						rt.scheduler.parkThread(this);
						pollCounter = pollingCount;
					} else {
						pollCounter--;
						Thread.yield();
					}
				}
			}
		}

		rt.scheduler.unregisterThread(this);
		//taskQueue = null;
	}

	/* External access for stealing a task from the current thread queue. */
	public final ImplicitTask tryStealingTask() {
		WorkStealingQueue<ImplicitTask> queue = taskQueue;
		if ( queue != null ) {
			return queue.tryStealing();
		}
		return null;
	}

	/* External access to look at the top of the queue. */
	public final ImplicitTask peekStealingTask() {
		WorkStealingQueue<ImplicitTask> queue = taskQueue;
		if ( queue != null ) {
			return queue.peekSteeling();
		}
		return null;
	}

	/* Returns the number of tasks in queue. */
	public final int getLocalQueueSize() {
		WorkStealingQueue<ImplicitTask> queue = taskQueue;
		if ( queue != null ) {
			return queue.size();
		}
		return 0;
	}

	/* A secondary loop inside the main loop that tries to fulfil a certain task
	 * before continuing on the outter loop. Supports nested loops.
	 *
	 * This method is only called when using the getResult() API.
	 */
	public final void progressToCompletion(ImplicitTask toComplete) {
		int pollCounter = pollingCount;
		while ( !toComplete.isCompleted() ) {
			ImplicitTask task = null;
			maxQueueSize = Math.max(maxQueueSize, taskQueue.size());
			task = taskQueue.pop();
			if ( task != null ) {
				task.invoke(rt);
				tasks++;

				if (enableProfiler) {
					if (task instanceof ImplicitAtomicTask)
						noAtomicTasksHandled.getAndIncrement();
					else if (task instanceof ImplicitBlockingTask)
						noBlockingTasksHandled.getAndIncrement();
					else if (task instanceof ImplicitNonBlockingTask)
						noNonBlockingTasksHandled.getAndIncrement();
				}
			} else {
				// scan for other queues
				task = rt.scheduler.scanQueues(this);
				steals++;
				if ( task != null ) {
					task.invoke(rt);
					tasks++;

					if (enableProfiler) {
						if (task instanceof ImplicitAtomicTask)
							noAtomicTasksHandled.getAndIncrement();
						else if (task instanceof ImplicitBlockingTask)
							noBlockingTasksHandled.getAndIncrement();
						else if (task instanceof ImplicitNonBlockingTask)
							noNonBlockingTasksHandled.getAndIncrement();
					}
				} else {
					nosteals++;
					if ( pollCounter == 0) {
						// reset counter
						pollCounter = pollingCount;
					} else {
						pollCounter--;
						Thread.yield();
					}
				}
			}
		}
	}

	public int getSteals() {
		return steals;
	}
	public int getNoSteals() {
		return nosteals;
	}
	public int getParks() {
		return parks;
	}
	public int getMaxQueueSize() {
		return maxQueueSize;
	}
	public int getTaskCount() {
		return tasks;
	}

	/* Added for profiler. */
	public final void getNoTasksHandled(int taskHandled[]) {

		taskHandled[DataCollection.ATOMIC_TASK] = this.noAtomicTasksHandled.get();
		taskHandled[DataCollection.BLOCKING_TASK] = this.noBlockingTasksHandled.get();
		taskHandled[DataCollection.NON_BLOCKING_TASK] = this.noNonBlockingTasksHandled.get();

	}

	public void setProfiler(AeminiumProfiler profiler) {
		this.profiler = profiler;
	}

	public final String toString() {
		return getName();
	}

	public void incrementNoBlockingTasksHandled() {
		this.noBlockingTasksHandled.getAndIncrement();
	}

	public void incrementNoNonBlockingTasksHandled() {
		this.noNonBlockingTasksHandled.getAndIncrement();
	}

	public void incrementNoAtomicTasksHandled() {
		this.noAtomicTasksHandled.getAndIncrement();
	}
}
