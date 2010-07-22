package aeminium.runtime.task;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;


public abstract class AbstractTaskFactory<T extends RuntimeTask> implements TaskFactory<T> {
	protected final EnumSet<Flags> flags;
	
	public AbstractTaskFactory(EnumSet<Flags> flags) {
		this.flags = flags;
	}

}
