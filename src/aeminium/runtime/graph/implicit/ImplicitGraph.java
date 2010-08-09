package aeminium.runtime.graph.implicit;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.Task;
import aeminium.runtime.graph.AbstractGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.implicit.ImplicitTask;

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
	public final void init() {
	}

	@Override
	public final void shutdown() {
	}
	
	@Override
	public final void addTask(T task, Task parent, Collection<T> deps) {
		taskCount.incrementAndGet();
		T itask = (T)task;

		itask.init(parent, prioritizer, this, deps);

		if ( checkForCycles ) {
			itask.checkForCycles();
		}
	}

	@Override
	public final void taskCompleted(T task) {
		if ( taskCount.decrementAndGet() == 0 ) {
			synchronized (this) {
				this.notifyAll();
			};
		}
	}
	
	@Override
	public final void waitToEmpty() {
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
