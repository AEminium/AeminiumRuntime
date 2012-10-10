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
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

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
	public int remainingRecursionDepth = Configuration.getProperty(getClass(), "maxRecursionDepth", 512);;
	protected WorkStealingQueue<ImplicitTask> taskQueue;
	protected static final AtomicInteger IdGenerator = new AtomicInteger(0);
	
	public WorkStealingThread(ImplicitWorkStealingRuntime rt, int index) {
		this.rt           = rt;
		this.index        = index;
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
		taskQueue = new ConcurrentWorkStealingQueue<ImplicitTask>(13);
		int pollCounter = pollingCount;
		rt.scheduler.registerThread(this);
		while (!shutdown) {
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
	
	public final String toString() {
		return getName();
	}
}
