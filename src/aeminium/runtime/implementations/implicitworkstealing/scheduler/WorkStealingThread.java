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
import aeminium.runtime.profiler.TaskInfo;

public final class WorkStealingThread extends AeminiumThread {
	protected final ImplicitWorkStealingRuntime rt;
	public final int index;
	protected volatile boolean shutdown = false;
	protected final int pollingCount   = Configuration.getProperty(getClass(), "pollingCount", 5);
	public int remainingRecursionDepth = Configuration.getProperty(getClass(), "maxRecursionDepth", 512);;
	protected WorkStealingQueue<ImplicitTask> taskQueue;
	protected static final AtomicInteger IdGenerator = new AtomicInteger(0);
	
	/* Profiler information. */
	volatile int noAtomicTasksHandled;
	volatile int noBlockingTasksHandled;
	volatile int noNonBlockingTasksHandled;
	
	protected final boolean enableProfiler = Configuration.getProperty(getClass(), "enableProfiler", true);
	protected AeminiumProfiler profiler;
	
	public WorkStealingThread(ImplicitWorkStealingRuntime rt, int index) {
		this.rt           = rt;
		this.index        = index;
		setName("WorkerStealingThread-"+IdGenerator.incrementAndGet());
		
		this.noAtomicTasksHandled = 0;
		this.noBlockingTasksHandled = 0;
		this.noNonBlockingTasksHandled = 0;
	}
	
	public final WorkStealingQueue<ImplicitTask> getTaskQueue() {
		return taskQueue;
	}
	
	public final void shutdown() {
		shutdown = true;
	}
	
	@Override
	public final void run() {
		super.run();
		taskQueue = new ConcurrentWorkStealingQueue<ImplicitTask>(13);
		int pollCounter = pollingCount;
		rt.scheduler.registerThread(this);
		while (!shutdown) {
			ImplicitTask task = null;
			task = taskQueue.pop();
			if ( task != null ) {
				
				if (enableProfiler) {
					TaskInfo info = this.profiler.getTaskInfo(task.hashCode());
					info.exitedQueue = System.nanoTime();
					info.startedExecution = System.nanoTime();
				}
				
				task.invoke(rt);
				
				if (enableProfiler) {
					TaskInfo info = this.profiler.getTaskInfo(task.hashCode());
					info.endedExecution = System.nanoTime();
					
					if (task instanceof ImplicitAtomicTask)
						noAtomicTasksHandled++;
					else if (task instanceof ImplicitBlockingTask)
						noBlockingTasksHandled++;
					else if (task instanceof ImplicitNonBlockingTask)
						noNonBlockingTasksHandled++;						
				}
				
				
			} else {
				// scan for other queues
				task = rt.scheduler.scanQueues(this);
				if ( task != null ) {
					if (enableProfiler) {
						TaskInfo info = this.profiler.getTaskInfo(task.hashCode());
						info.exitedQueue = System.nanoTime();
						info.startedExecution = System.nanoTime();
					}
					
					task.invoke(rt);
					
					if (enableProfiler) {
						TaskInfo info = this.profiler.getTaskInfo(task.hashCode());
						info.endedExecution = System.nanoTime();
						
						if (task instanceof ImplicitAtomicTask)
							noAtomicTasksHandled++;
						else if (task instanceof ImplicitBlockingTask)
							noBlockingTasksHandled++;
						else if (task instanceof ImplicitNonBlockingTask)
							noNonBlockingTasksHandled++;
					}
					
				} else {
					if ( pollCounter == 0) {
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
		taskQueue = null;
	}

	public final ImplicitTask tryStealingTask() {
		WorkStealingQueue<ImplicitTask> queue = taskQueue;
		if ( queue != null ) {
			return queue.tryStealing();
		}
		return null;
	}
	
	public final ImplicitTask peekStealingTask() {
		WorkStealingQueue<ImplicitTask> queue = taskQueue;
		if ( queue != null ) {
			return queue.peekSteeling();
		}
		return null;
	}
	
	public final int getLocalQueueSize() {
		WorkStealingQueue<ImplicitTask> queue = taskQueue;
		if ( queue != null ) {
			return queue.size();
		}
		return 0;
	}
	
	public final void progressToCompletion(ImplicitTask toComplete) {
		int pollCounter = pollingCount;
		while ( !toComplete.isCompleted() ) {
			ImplicitTask task = null;
			task = taskQueue.pop();
			if ( task != null ) {
				task.invoke(rt);
			} else {
				// scan for other queues
				task = rt.scheduler.scanQueues(this);
				if ( task != null ) {
					task.invoke(rt);
				} else {
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
	
	public final void getNoTasksHandledAndReset(int taskHandled[]) {
		
		taskHandled[DataCollection.ATOMIC_TASK] = this.noAtomicTasksHandled;
		taskHandled[DataCollection.BLOCKING_TASK] = this.noBlockingTasksHandled;
		taskHandled[DataCollection.NON_BLOCKING_TASK] = this.noNonBlockingTasksHandled;
		
		this.noAtomicTasksHandled = 0;
		this.noBlockingTasksHandled = 0;
		this.noNonBlockingTasksHandled = 0;
	}
	
	public void setProfiler(AeminiumProfiler profiler) {
		this.profiler = profiler;
	}
	
	public final String toString() {
		return getName();
	}
}
