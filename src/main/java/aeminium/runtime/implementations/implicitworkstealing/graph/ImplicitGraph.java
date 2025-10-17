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

package aeminium.runtime.implementations.implicitworkstealing.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.events.EventManager;
import aeminium.runtime.implementations.implicitworkstealing.events.RuntimeEventListener;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.AeminiumThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitAtomicTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitBlockingTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitNonBlockingTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTaskState;
import aeminium.runtime.profiler.AeminiumProfiler;
import aeminium.runtime.profiler.DataCollection;

/*
 * This Graph hosts task information and pre and post processes
 * tasks before and after scheduling.
 */
public class ImplicitGraph {
	protected final ImplicitWorkStealingRuntime rt;
	protected List<TaskCounter> taskCounterList;
	protected ThreadLocal<TaskCounter> taskCounters;
	protected final boolean checkForCycles;
	protected final int pollingTimeout;
	protected final boolean debug;
	protected boolean polling = false;

	/* Profiler information. */
	protected AeminiumProfiler profiler;
	protected final boolean enableProfiler = Configuration.getProperty(getClass(), "enableProfiler", false);

	private AtomicInteger noAtomicTasksCompleted = new AtomicInteger(0);
	private AtomicInteger noBlockingTasksCompleted = new AtomicInteger(0);
	private AtomicInteger noNonBlockingTasksCompleted = new AtomicInteger(0);

	public AtomicInteger noUnscheduledTasks = new AtomicInteger(0);
	public AtomicInteger noRunningTasks = new AtomicInteger(0);
	public AtomicInteger noWaitingForDependenciesTasks = new AtomicInteger(0);
	public AtomicInteger noWaitingForChildrenTasks = new AtomicInteger(0);
	public AtomicInteger noTasksWaitingInQueue = new AtomicInteger(0);
	public AtomicInteger noCompletedTasks = new AtomicInteger(0);

	private static final class TaskCounter {
		protected final Thread thread;
		public volatile int taskCount = 0;

		public TaskCounter(Thread thread) {
			this.thread = thread;
		}

		public final int getTaskCount() {
			if ( thread instanceof AeminiumThread ) {
				return ((AeminiumThread)thread).taskCount;
			} else {
				return taskCount;
			}
		}

		@Override
		public final String toString() {
			return "TaskCounter<"+getTaskCount()+">";
		}
	}

	public ImplicitGraph(ImplicitWorkStealingRuntime rt) {
		this.rt = rt;
		debug          = Configuration.getProperty(getClass(), "debug", false);
		checkForCycles = Configuration.getProperty(getClass(), "checkForCycles", false);
		pollingTimeout = Configuration.getProperty(getClass(), "pollingTimeout", 50);
	}

	public final void init(EventManager eventManager) {
		taskCounterList  = new ArrayList<TaskCounter>();
		taskCounters     = new ThreadLocal<TaskCounter>(){
			@Override
			protected TaskCounter initialValue() {
				TaskCounter tc = new TaskCounter(Thread.currentThread());
				synchronized (taskCounterList) {
					taskCounterList.add(tc);
				}
				return tc;
			}
		};
		eventManager.registerRuntimeEventListener(new RuntimeEventListener() {
			@Override
			public final void onThreadSuspend(Thread thread) {
				synchronized (taskCounterList) {
					if ( isEmpty() ) {
						taskCounterList.notifyAll();
					}
				}
			}

			@Override
			public final void onPolling() {
				polling = true;
			}

			@Override
			public final void onNewThread(Thread thread) {
				// make sure we created initial value inside the thread
				taskCounters.get();
			}
		});
	}

	public final void shutdown() {
		taskCounterList = null;
		taskCounters    = null;
	}

	/*
	 * Sets up a new task in relation to another tasks (dependents and dependencies)
	 * and schedules it for execution if it is the case.
	 */
	public final void addTask(ImplicitTask itask, Task parent, Collection<Task> deps) {

		// update thread specific task counter
		Thread thread = Thread.currentThread();
		if ( thread instanceof AeminiumThread ) {
			((AeminiumThread)thread).taskCount++;
		} else {
			taskCounters.get().taskCount++;
		}

		boolean schedule = false;

		synchronized (itask) {
			// check for double scheduling
			if ( itask.getState() != ImplicitTaskState.UNSCHEDULED) {
				if ( thread instanceof AeminiumThread ) {
					((AeminiumThread)thread).taskCount--;
				} else {
					taskCounters.get().taskCount--;
				}
				rt.getErrorManager().signalTaskDuplicatedSchedule(itask);
				return;
			}

			if (enableProfiler)
				this.noUnscheduledTasks.incrementAndGet();

			// setup parent connection
			if ( parent != Runtime.NO_PARENT ) {
				ImplicitTask iparent = (ImplicitTask) parent;
				itask.parent = iparent;
				itask.level = (short) (iparent.level + 1);
				itask.parent.attachChild(rt, itask);
			}

			// setup dependencies
			if (enableProfiler)
				itask.setState(ImplicitTaskState.WAITING_FOR_DEPENDENCIES, this);
			else
				itask.setState(ImplicitTaskState.WAITING_FOR_DEPENDENCIES);


			if ( (Object)deps != Runtime.NO_DEPS ) {
				int count = 0;
				for ( Task t : deps) {
					if (t!= null) {
						ImplicitTask it = (ImplicitTask)t;
						count += it.addDependent(itask);
					}
				}
				itask.depCount = count;
				if ( itask.depCount == 0 ) {
					schedule = true;
				}
			} else {
				schedule = true;
			}
		}

		// schedule task if it's marked as waiting in queue
		if (schedule) {
			if (enableProfiler)
				itask.setState(ImplicitTaskState.WAITING_IN_QUEUE, this);
			else
				itask.setState(ImplicitTaskState.WAITING_IN_QUEUE);

			rt.scheduler.scheduleTask(itask);
		}

		if ( checkForCycles ) {
			itask.checkForCycles(rt.getErrorManager());
			return;
		}
	}


	/* taskCompleted is executed when a task has completed */
	public final void taskCompleted(ImplicitTask task) {
		Thread thread = Thread.currentThread();
		if ( thread instanceof AeminiumThread ) {
			((AeminiumThread)thread).taskCount--;
		} else {
			taskCounters.get().taskCount--;
		}

		if (enableProfiler) {
			if (task instanceof ImplicitAtomicTask)
				this.noAtomicTasksCompleted.getAndIncrement();
			else if (task instanceof ImplicitBlockingTask)
				this.noBlockingTasksCompleted.getAndIncrement();
			else if (task instanceof ImplicitNonBlockingTask)
				this.noNonBlockingTasksCompleted.getAndIncrement();
		}
	}

	protected final boolean isEmpty() {
		int count = 0;
		for(TaskCounter tc : taskCounterList ) {
			count += tc.getTaskCount();
		}
		return count == 0;
	}

	/* Blocks until all tasks have completed. */
	public final void waitToEmpty() {
		synchronized (taskCounterList) {
			boolean empty = false;
			while ( !empty ) {
				empty = isEmpty();
				if ( !empty ) {
					try {
						if ( polling || debug ) {
							taskCounterList.wait(pollingTimeout);
						} else {
							taskCounterList.wait();
						}
					} catch (InterruptedException e) {}
				}
			}
		}
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *                                          PROFILER                                               *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	public synchronized void collectData(DataCollection data) {

		data.noTasksCompleted[DataCollection.ATOMIC_TASK] = this.noAtomicTasksCompleted.get();
		data.noTasksCompleted[DataCollection.BLOCKING_TASK] = this.noBlockingTasksCompleted.get();
		data.noTasksCompleted[DataCollection.NON_BLOCKING_TASK] = this.noNonBlockingTasksCompleted.get();

		data.noUnscheduledTasks = this.noUnscheduledTasks.get();
		data.noWaitingForDependenciesTasks = this.noWaitingForDependenciesTasks.get();
		data.noWaitingForChildrenTasks = this.noWaitingForChildrenTasks.get();
		data.noRunningTasks = this.noRunningTasks.get();
		data.noTasksWaitingInQueue = this.noTasksWaitingInQueue.get();
		data.noCompletedTasks = this.noCompletedTasks.get();

		return;
	}

	public void setProfiler (AeminiumProfiler profiler) {
		this.profiler = profiler;
	}

}
