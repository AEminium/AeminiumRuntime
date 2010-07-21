package aeminium.runtime.prioritizer;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractPrioritizer<T extends RuntimeTask> implements RuntimePrioritizer<T> {
	protected final EnumSet<Flags> flags;
	
	public AbstractPrioritizer(EnumSet<Flags> flags) {
		this.flags = flags;
	}
}
