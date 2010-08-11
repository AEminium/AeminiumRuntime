package aeminium.runtime.datagroup;

import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractDataGroup<T extends RuntimeTask> implements RuntimeDataGroup<T> {
	protected final RuntimeScheduler<T> scheduler;
	
	public AbstractDataGroup(RuntimeScheduler<T> scheduler) {
		this.scheduler = scheduler;
	}
}
