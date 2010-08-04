package aeminium.runtime.graph.implicit;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.RuntimeError;
import aeminium.runtime.Task;
import aeminium.runtime.graph.AbstractGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.TaskDescription;
import aeminium.runtime.task.implicit.ImplicitTask;
import aeminium.runtime.task.implicit.ImplicitTaskState;

@SuppressWarnings("unchecked")
public class ImplicitGraph<T extends ImplicitTask> extends AbstractGraph<T> {
	protected AtomicInteger taskCount = new AtomicInteger(0);
	protected final boolean checkForCycles;
	
	public ImplicitGraph(RuntimePrioritizer<T> prioritizer,  EnumSet<Flags> flags) {
		super(prioritizer, flags);
		if ( flags.contains(Flags.CHECK_FOR_CYCLES)) {
			checkForCycles = true;
		} else {
			checkForCycles = false;
		}
	}
	
	@Override
	public void addTask(T task, Task parent, Collection<T> deps) {
		taskCount.incrementAndGet();
		T itask = (T)task;
				
		synchronized (itask) {
			if ( itask.getTaskState() != ImplicitTaskState.UNSCHEDULED) {
				throw new RuntimeError("Cannot schedule task twice: " + task);
			}

			// set prioritizer
			itask.setPrioritizer(prioritizer);

			// set graph
			itask.setGraph(this);
			
			// set parent
			itask.setParent(parent);
			
			// update dependency count and eventually schedule 
			itask.setDependencies(deps);
			
			if ( checkForCycles ) {
				itask.checkForCycles();
			}			
		}
	}

	@Override
	public TaskDescription<T> getTaskDescription(T task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void taskFinished(T task) {
		task.taskFinished();
		if ( taskCount.decrementAndGet() == 0 ) {
			synchronized (this) {
				this.notifyAll();
			};
		}
	}
	

	@Override
	public void waitToEmpty() {
		while ( taskCount.get() != 0 ) {
			synchronized (this) {
				try {
					if (flags.contains(Flags.DEBUG)) {
						this.wait(1000);
					} else {
						this.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
