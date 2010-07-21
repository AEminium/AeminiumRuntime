package aeminium.runtime.scheduler;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractScheduler<T extends RuntimeTask> implements RuntimeScheduler<T>, RuntimePrioritizer<T> {
	protected final EnumSet<Flags> flags;
	
	public AbstractScheduler(EnumSet<Flags> flags) {
		this.flags = flags;
	}
}
