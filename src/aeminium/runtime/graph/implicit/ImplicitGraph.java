package aeminium.runtime.graph.implicit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import aeminium.runtime.Runtime;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.Task;
import aeminium.runtime.events.RuntimeEventListener;
import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.graph.AbstractGraph;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.scheduler.AeminiumThread;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.implicit.ImplicitTask;
import aeminium.runtime.task.implicit.ImplicitTaskState;


@SuppressWarnings("unchecked")
public class ImplicitGraph<T extends ImplicitTask> extends AbstractGraph<T> {
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

	public ImplicitGraph(RuntimePrioritizer<T> prioritizer) {
		super(prioritizer);
		debug          = Configuration.getProperty(getClass(), "debug", false);
		checkForCycles = Configuration.getProperty(getClass(), "checkForCycles", false);
		pollingTimeout = Configuration.getProperty(getClass(), "pollingTimeout", 50);
	}
	
	@Override
	public final void init(RuntimeEventManager eventManager) {
		super.init();
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
				TaskCounter tc = taskCounters.get();
			}
		});
	}

	@Override
	public final void shutdown() {
		taskCounterList = null;
		taskCounters    = null;
	}
	
	@Override
	public final void addTask(T task, Task parent, Collection<T> deps) {

		// update thread specific task counter
		Thread thread = Thread.currentThread();
		if ( thread instanceof AeminiumThread ) {
			((AeminiumThread)thread).taskCount++;
		} else {
			taskCounters.get().taskCount++;
		}
		
		boolean schedule = false;
		T itask = (T)task;
		synchronized (itask) {			
			// check for double scheduling
			if ( itask.state != ImplicitTaskState.UNSCHEDULED) {
				throw new RuntimeError("Cannot schedule task twice: " + this);
			}

			// setup parent connection
			if ( parent != Runtime.NO_PARENT ) {
				T Tparent = (T) parent;
				itask.parent = Tparent;
				itask.level = (short) (Tparent.level + 1);
				itask.parent.attachChild(itask);
			}

			// setup dependencies
			itask.state = ImplicitTaskState.WAITING_FOR_DEPENDENCIES;
			if ( (Object)deps != Runtime.NO_DEPS ) {
				int count = 0;
				for ( RuntimeTask t : deps) {
					count += ((ImplicitTask) t).addDependent(itask);						
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
			prioritizer.scheduleTask(itask);	
		}

		if ( checkForCycles ) {
			itask.checkForCycles();
		}
	}

	@Override
	public final void taskCompleted(T task) {
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
	
	@Override
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
