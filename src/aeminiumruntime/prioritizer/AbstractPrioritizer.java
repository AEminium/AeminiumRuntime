package aeminiumruntime.prioritizer;

import java.util.EnumSet;

import aeminiumruntime.implementations.Flag;
import aeminiumruntime.task.RuntimeTask;

public abstract class AbstractPrioritizer<T extends RuntimeTask> implements RuntimePrioritizer<T> {
	protected final EnumSet<Flag> flags;
	
	public AbstractPrioritizer(EnumSet<Flag> flags) {
		this.flags = flags;
	}
}
