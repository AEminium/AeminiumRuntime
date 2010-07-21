package aeminium.runtime.scheduler;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flag;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractScheduler<T extends RuntimeTask> implements RuntimeScheduler<T>, RuntimePrioritizer<T> {
	protected final EnumSet<Flag> flags;
	
	public AbstractScheduler(EnumSet<Flag> flags) {
		this.flags = flags;
	}
}