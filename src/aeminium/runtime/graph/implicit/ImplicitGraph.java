package aeminium.runtime.graph.implicit;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;

import aeminium.runtime.Task;
import aeminium.runtime.graph.AbstractGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.implicit.ImplicitTask;
import aeminium.runtime.taskcounter.RuntimeTaskCounter;
import aeminium.runtime.taskcounter.TaskCountingThread;

@SuppressWarnings("unchecked")
public class ImplicitGraph<T extends ImplicitTask> extends AbstractGraph<T> {
	protected final boolean checkForCycles;
	protected RuntimeTaskCounter taskCounter;
	protected volatile long taskCount = 0;
	
	public ImplicitGraph(RuntimePrioritizer<T> prioritizer,  EnumSet<Flags> flags) {
		super(prioritizer, flags);
		if ( flags.contains(Flags.CHECK_FOR_CYCLES)) {
			checkForCycles = true;
		} else {
			checkForCycles = false;
		}
	}
	
	@Override
	public final void init(RuntimeTaskCounter tc) {
		taskCounter = tc;
		taskCount = 0;
	}

	@Override
	public final void shutdown() {
	}
	
	protected final void updateTaskCount(int delta) {
		Thread thread = Thread.currentThread();
		if (  thread instanceof TaskCountingThread ) {
			TaskCountingThread tct = (TaskCountingThread)thread;
			tct.tasksAdded(delta);
		} else {
			taskCount += delta;
		}
	}
	
	@Override
	public final void addTask(T task, Task parent, Collection<T> deps) {
		
		updateTaskCount(1);
		T itask = (T)task;

		itask.init(parent, prioritizer, this, deps);

		if ( checkForCycles ) {
			itask.checkForCycles();
		}
	}

	@Override
	public final void taskCompleted(T task) {
		updateTaskCount(-1);
	}
	
	@Override
	public final void waitToEmpty() {
		taskCounter.waitToEmpty(taskCount);
	}

}
