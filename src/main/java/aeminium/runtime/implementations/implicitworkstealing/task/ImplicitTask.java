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

package aeminium.runtime.implementations.implicitworkstealing.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import aeminium.runtime.Body;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.error.ErrorManager;
import aeminium.runtime.implementations.implicitworkstealing.graph.ImplicitGraph;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;


public abstract class ImplicitTask implements Task
{
	protected static final Object UNSET = new Object()
	{
		@Override
		public String toString()
		{
			return "UNSET";
		}
	};

	protected volatile Object result = UNSET;  // could merge result with body
	public Body body;
	private ImplicitTaskState state = ImplicitTaskState.UNSCHEDULED;  // could be a byte instead of a reference
	public volatile int depCount;
	public int childCount;
	public List<ImplicitTask> dependents;
	public List<ImplicitTask> children;     // children are only used for debugging purposes => could be removed
	public ImplicitTask parent;
	public static final boolean debug = Configuration.getProperty(ImplicitTask.class, "debug", false);
	public final boolean enableProfiler;
	public final short hints;
	public short level;
	public Thread waiter;    // we could same this and just mention that there is someone waiting
	public Runnable finishedCallback;

	/* Added for profiler. */
	public int id;

	public ImplicitTask(Body body, short hints, boolean enableProfiler) {
		this.body = body;
		this.hints = hints;
		this.enableProfiler = enableProfiler;
	}

	public void invoke(ImplicitWorkStealingRuntime rt) {

		if (enableProfiler)
			this.setState(ImplicitTaskState.RUNNING, rt.graph);
		else
			this.setState(ImplicitTaskState.RUNNING);

		try {
			body.execute(rt, this);
		} catch (Throwable e)
		{
			rt.getErrorManager().signalTaskException(this, e);
			setResult(e);
		} finally
		{
			taskFinished(rt);
		}
	}

	@Override
	public final void setResult(Object result)
	{
		this.result = result;
	}

	@Override
	public final Object getResult()
	{
		if (this.isCompleted())
		{
			return result;
		} else
		{
			Thread thread = Thread.currentThread();
			if ( thread instanceof WorkStealingThread )
			{
				((WorkStealingThread)thread).progressToCompletion(this);
			} else
			{
				synchronized (this)
				{
					while (!isCompleted())
					{
						waiter = thread;

						try
						{
							this.wait();
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
			}

			return result;
		}
	}

	public final void attachChild(ImplicitWorkStealingRuntime rt, ImplicitTask child)
	{
		synchronized (this)
		{
			childCount += 1;

			if (debug)
			{
				if (children == null )
					children = new ArrayList<ImplicitTask>(10);

				children.add(child);
			}
		}
	}

	public final int addDependent(ImplicitTask task)
	{
		synchronized (this)
		{
			if ( state == ImplicitTaskState.COMPLETED)
				return 0;

			if (this.dependents == null)
				this.dependents = new ArrayList<ImplicitTask>();

			this.dependents.add(task);
			//System.err.println("Added " + task.id + " to dependents of " + this.id);
			return 1;
		}
	}

	public final void decDependencyCount(ImplicitWorkStealingRuntime rt)
	{
		boolean schedule = false;
		synchronized (this)
		{
			depCount -= 1;
			//System.err.println("Decrementing " + this.id + " - " + depCount);

			if ( depCount == 0 ) {
				if (enableProfiler) {
					this.setState(ImplicitTaskState.WAITING_IN_QUEUE, rt.graph);
				} else {
					this.setState(ImplicitTaskState.WAITING_IN_QUEUE);
				}
				schedule = true;
			}
		}
		if ( schedule ) {
			//System.err.println("Scheduling " + this.id);
			rt.scheduler.scheduleTask(this);
		}
	}

	public final void taskFinished(ImplicitWorkStealingRuntime rt)
	{

		boolean completed = false;
		synchronized (this) {
			if (childCount == 0) {
				completed = true;
			} else {
				if (enableProfiler) {
					this.setState(ImplicitTaskState.WAITING_FOR_CHILDREN, rt.graph);
				} else {
					this.setState(ImplicitTaskState.WAITING_FOR_CHILDREN);
				}
			}
		}
		if (completed)
			taskCompleted(rt);
	}

	public boolean detachChild(ImplicitWorkStealingRuntime rt)
	{

		synchronized(this)
		{
			this.childCount--;

			if (this.childCount <= 0 && state == ImplicitTaskState.WAITING_FOR_CHILDREN)
				return true;
		}

		return false;
	}

	public void taskCompleted(ImplicitWorkStealingRuntime rt)
	{
		ImplicitTask task = this;
		ImplicitTask next;

		do
		{
			synchronized(task)
			{
				if (enableProfiler) {
					this.setState(ImplicitTaskState.COMPLETED, rt.graph);
				} else {
					this.setState(ImplicitTaskState.COMPLETED);
				}
			}

			if (task.parent != null && task.parent.detachChild(rt))
				next = task.parent;
			else
				next = null;

			if (task.dependents != null)
			{
				for (ImplicitTask t : task.dependents)
					t.decDependencyCount(rt);

				task.dependents = null;
			}

			// callback
			if (finishedCallback != null) {
				finishedCallback.run();
				finishedCallback = null;
			}

			// cleanup references
			task.body = null;
			task.children = null;

			rt.graph.taskCompleted(task);

			if (task.waiter != null) {
				synchronized(this) {
					notifyAll();
				}
			}

			task = next;
		} while (task != null);
	}

	public final boolean isCompleted()
	{
		return state == ImplicitTaskState.COMPLETED;
	}

	public void checkForCycles(final ErrorManager em)
	{
		synchronized (this)
		{
			checkForCycles(this, dependents, em);
		}
	}

	protected void checkForCycles(final ImplicitTask task, final Collection<ImplicitTask> deps, final ErrorManager em)
	{
		if (deps == null)
			return;

		for (ImplicitTask t : deps)
			checkPath(task, t, em);
	}

	protected void checkPath(final ImplicitTask task, ImplicitTask dep, final ErrorManager em)
	{
		if (task == dep)
		{
			em.signalDependencyCycle(task);
		} else
		{
			Collection<ImplicitTask> nextDependents;
			synchronized (dep)
			{
				nextDependents = Collections.unmodifiableList(dep.dependents);
			}

			checkForCycles(task, nextDependents, em);
		}
	}

	/* This method simply changes the state of the task. */
	public void setState(ImplicitTaskState newState) {
		this.state = newState;
	}

	/* If we are using the profiler, we need to call these methods, because we need to update
	 * the value of the graph variables concerning the actual state, namely, for example, the
	 * number of running tasks or the number of tasks waiting for dependencies.
	 */
	public void setState(ImplicitTaskState newState, ImplicitGraph graph) {

		/* First, we have to test the previous state, decreasing the corresponding
		 * number of tasks in the graph for that category.
		 */
		if (this.state == ImplicitTaskState.UNSCHEDULED) {
			graph.noUnscheduledTasks.decrementAndGet();
		} else if (this.state == ImplicitTaskState.WAITING_IN_QUEUE) {
				graph.noTasksWaitingInQueue.decrementAndGet();
		} else if (this.state == ImplicitTaskState.RUNNING) {
			graph.noRunningTasks.decrementAndGet();
		} else if (this.state == ImplicitTaskState.WAITING_FOR_DEPENDENCIES) {
			graph.noWaitingForDependenciesTasks.decrementAndGet();
		} else if (this.state == ImplicitTaskState.WAITING_FOR_CHILDREN) {
			graph.noWaitingForChildrenTasks.decrementAndGet();
		} else if (this.state == ImplicitTaskState.COMPLETED) {
			graph.noCompletedTasks.decrementAndGet();
		}

		/* Update the task state. */
		this.state = newState;

		/* Having the new state, we also need to update the graph counters. */
		if (this.state == ImplicitTaskState.UNSCHEDULED) {
			graph.noUnscheduledTasks.incrementAndGet();
		} else if (this.state == ImplicitTaskState.WAITING_IN_QUEUE) {
				graph.noTasksWaitingInQueue.incrementAndGet();
		} else if (this.state == ImplicitTaskState.RUNNING) {
			graph.noRunningTasks.incrementAndGet();
		} else if (this.state == ImplicitTaskState.WAITING_FOR_DEPENDENCIES) {
			graph.noWaitingForDependenciesTasks.incrementAndGet();
		} else if (this.state == ImplicitTaskState.WAITING_FOR_CHILDREN) {
			graph.noWaitingForChildrenTasks.incrementAndGet();
		} else if (this.state == ImplicitTaskState.COMPLETED) {
			graph.noCompletedTasks.incrementAndGet();
		}
	}

	public ImplicitTaskState getState() {
		return this.state;
	}

	public void setFinishedCallback(Runnable r) {
		finishedCallback = r;
	}


	@Override
	public String toString()
	{
		return "Task<"+body+">[children:"+childCount+", deps:"+depCount+", state:"+state+"]";
	}
}
