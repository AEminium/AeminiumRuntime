package aeminium.runtime.implementations.implicitworkstealing.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.events.EventManager;
import aeminium.runtime.implementations.implicitworkstealing.events.RuntimeEventListener;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.AeminiumThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTaskState;


public class ImplicitGraph {
	protected final ImplicitWorkStealingRuntime rt;
	protected List<TaskCounter> taskCounterList;
	protected ThreadLocal<TaskCounter> taskCounters;
	protected final boolean checkForCycles;
	protected final int pollingTimeout;
	protected final boolean debug;
	protected boolean polling = false;
	
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
			if ( itask.state != ImplicitTaskState.UNSCHEDULED) {
				if ( thread instanceof AeminiumThread ) {
					((AeminiumThread)thread).taskCount--;
				} else {
					taskCounters.get().taskCount--;
				}
				rt.getErrorManager().signalTaskDuplicatedSchedule(itask);
				return;
			}

			// setup parent connection
			if ( parent != Runtime.NO_PARENT ) {
				ImplicitTask iparent = (ImplicitTask) parent;
				itask.parent = iparent;
				itask.level = (short) (iparent.level + 1);
				itask.parent.attachChild(rt, itask);
			}

			// setup dependencies
			itask.state = ImplicitTaskState.WAITING_FOR_DEPENDENCIES;
			if ( (Object)deps != Runtime.NO_DEPS ) {
				int count = 0;
				for ( Task t : deps) {
					ImplicitTask it = (ImplicitTask)t;
					count += it.addDependent(itask);						
				}
				itask.depCount += count;
				if ( itask.depCount == 0 ) {
					itask.state = ImplicitTaskState.RUNNING;
					schedule = true;
				}
			} else {
				itask.state = ImplicitTaskState.RUNNING;
				schedule = true;
			}			
		}
		
		// schedule task if it's marked as running
		if (schedule) {
			rt.scheduler.scheduleTask(itask);
		}

		if ( checkForCycles ) {
			itask.checkForCycles(rt.getErrorManager());
			return;
		}
	}

	public final void taskCompleted(ImplicitTask task) {
		Thread thread = Thread.currentThread();
		if ( thread instanceof AeminiumThread ) {
			((AeminiumThread)thread).taskCount--;
		} else {
			taskCounters.get().taskCount--;
		}
	}
	
	protected final boolean isEmpty() {
		int count = 0;
		for(TaskCounter tc : taskCounterList ) {
			count += tc.getTaskCount();
		}
		return count == 0;
	}

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

}
