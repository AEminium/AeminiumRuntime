package aeminium.runtime.datagroup;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractDataGroupFactory<T extends RuntimeTask> implements DataGroupFactory<T> {
	protected final RuntimeScheduler<T> scheduler;
	protected final EnumSet<Flags> flags;
	
	public AbstractDataGroupFactory(RuntimeScheduler<T> scheduler, EnumSet<Flags> flags) {
		this.scheduler = scheduler;
		this.flags = flags;
	}
}
