package aeminium.runtime.graph.implicit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import aeminium.runtime.Task;
import aeminium.runtime.events.RuntimeEventListener;
import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.graph.AbstractGraph;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.implicit.ImplicitTask;

class TaskCounter {
	public volatile int taskCount = 0;
}

@SuppressWarnings("unchecked")
public class ImplicitGraph<T extends ImplicitTask> extends AbstractGraph<T> {
	protected final List<TaskCounter> taskCounterList = new ArrayList<TaskCounter>(); 
	protected ThreadLocal<TaskCounter> taskcounters= new ThreadLocal<TaskCounter>(){
		@Override
		protected TaskCounter initialValue() {
			TaskCounter tc = new TaskCounter();
			synchronized (taskCounterList) {
				taskCounterList.add(tc);
			}
			return tc;
		}		
	};
	protected final boolean checkForCycles;
	protected final int pollingTimeout;
	protected boolean polling = false;
	
	public ImplicitGraph(RuntimePrioritizer<T> prioritizer) {
		super(prioritizer);
		checkForCycles = Configuration.getProperty(getClass(), "checkForCycles", false);
		pollingTimeout = Configuration.getProperty(getClass(), "pollingTimeout", 50);
	}
	
	@Override
	public final void init(RuntimeEventManager eventManager) {
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
			public void onPolling() {
				polling = true;
			}
		});
	}

	@Override
	public final void shutdown() {
	}
	
	@Override
	public final void addTask(T task, Task parent, Collection<T> deps) {

		taskcounters.get().taskCount++;
		
		T itask = (T)task;

		itask.init(parent, prioritizer, this, deps);

		if ( checkForCycles ) {
			itask.checkForCycles();
		}
	}

	@Override
	public final void taskCompleted(T task) {
		taskcounters.get().taskCount--;
	}
	
	protected boolean isEmpty() {
		int count = 0;
		for(TaskCounter tc : taskCounterList ) {
			count += tc.taskCount;
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
						if ( polling ) {
							taskCounterList.wait(pollingTimeout);
						} else {
							taskCounterList.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
