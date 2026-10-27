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

import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.events.EventManager;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitBlockingTask;

/* Threadpool for executing Blocking Tasks.
 * Has a traditional threadpool behavior.
 */
public final class BlockingThreadPool {
	protected ImplicitWorkStealingRuntime rt;
	protected EventManager eventManager;
	private List<ImplicitBlockingTask> taskQueue;
	private int currentThreads;
	private int sleepingThreads;
	protected static int maxThreads               = Configuration.getProperty(BlockingThreadPool.class, "maxThreads", Runtime.getRuntime().availableProcessors()*2);
	protected final ImplicitBlockingTask FINISHED = new ImplicitBlockingTask(null, (short)0, this.rt.enableProfiler) {
		@Override
		public String toString() {
			return "FINISHED";
		}
	};

	public void init(ImplicitWorkStealingRuntime rt, EventManager eventManager) {
		this.rt   = rt;
		this.eventManager = eventManager;
		taskQueue = new LinkedList<ImplicitBlockingTask>();
	}

	public void shutdown() {
		synchronized (taskQueue) {
			int finishedCount = currentThreads;
			for( int i = 0; i < finishedCount; i++ ) {
				submitTask(FINISHED);
			}

			while ( currentThreads > 0 ) {
				try {
					taskQueue.wait();
				} catch (InterruptedException e) {
				}
			}
		}

		// cleanup
		taskQueue    = null;
		rt           = null;
		eventManager = null;
	}
	/* Retrieves a new task to execute from the queue. */
	protected ImplicitBlockingTask getWork() {
		synchronized (taskQueue) {
			if ( taskQueue.isEmpty() ) {
				ImplicitBlockingTask task = null;
				while ( task == null ) {
					try {
						sleepingThreads++;
						eventManager.signalThreadSuspend(Thread.currentThread());
						taskQueue.wait();
						if ( !taskQueue.isEmpty() ) {
							task = taskQueue.remove(0);
						}
					} catch (InterruptedException e) {
						// wait more
					} finally {
						sleepingThreads--;
					}
				}
				return task;
			} else {
				return taskQueue.remove(0);
			}
		}
	}

	/* Is called after a task finishes its work. */
	protected void signalThreadFinished() {
		synchronized (taskQueue) {
			currentThreads--;
			if ( currentThreads == 0 ) {
				taskQueue.notifyAll();
			}
		}
	}


	/* Adds a new task to the queue. */
	public final void submitTask(ImplicitBlockingTask task) {
		synchronized (taskQueue) {
			taskQueue.add(task);
			if ( sleepingThreads > 0 ) {
				taskQueue.notify();
			} else {
				if ( currentThreads < maxThreads ) {
					// create new thread
					new BlockingThread().start();
					currentThreads++;
				}
			}
		}
	}

	public int getTaskQueueSize() {
		return taskQueue.size();
	}

	protected final class BlockingThread extends AeminiumThread {
		protected boolean finished = false;

		public BlockingThread() {
			setName("BlockingThead-" + currentThreads);
		}

		@Override
		public void run() {
			eventManager.signalNewThread(Thread.currentThread());
			while ( !finished ) {
				ImplicitBlockingTask task = getWork();
				if ( task != FINISHED ) {
					task.invoke(rt);
					task = null;
				} else {
					finished = true;
				}
			}
			signalThreadFinished();
		}
	}
}
