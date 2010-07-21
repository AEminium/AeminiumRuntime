package aeminium.runtime.prioritizer;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flag;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractPrioritizer<T extends RuntimeTask> implements RuntimePrioritizer<T> {
	protected final EnumSet<Flag> flags;
	
	public AbstractPrioritizer(EnumSet<Flag> flags) {
		this.flags = flags;
	}
}
