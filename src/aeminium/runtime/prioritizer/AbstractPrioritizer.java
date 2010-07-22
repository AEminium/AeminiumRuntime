package aeminium.runtime.prioritizer;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractPrioritizer<T extends RuntimeTask> implements RuntimePrioritizer<T> {
	protected final EnumSet<Flags> flags;
	protected final RuntimeScheduler<T> scheduler;
	
	public AbstractPrioritizer(RuntimeScheduler<T> scheduler, EnumSet<Flags> flags) {
		this.scheduler = scheduler;
		this.flags = flags;
	}
}
