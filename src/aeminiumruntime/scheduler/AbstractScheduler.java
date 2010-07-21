package aeminiumruntime.scheduler;

import java.util.EnumSet;

import aeminiumruntime.implementations.Flag;
import aeminiumruntime.prioritizer.RuntimePrioritizer;
import aeminiumruntime.task.RuntimeTask;

public abstract class AbstractScheduler<T extends RuntimeTask> implements RuntimeScheduler<T>, RuntimePrioritizer<T> {
	protected final EnumSet<Flag> flags;
	
	public AbstractScheduler(EnumSet<Flag> flags) {
		this.flags = flags;
	}
}
