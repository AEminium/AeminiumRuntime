package aeminium.runtime.datagroup;

import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractDataGroupFactory<T extends RuntimeTask> implements DataGroupFactory<T> {
	protected final RuntimeScheduler<T> scheduler;
	
	public AbstractDataGroupFactory(RuntimeScheduler<T> scheduler) {
		this.scheduler = scheduler;
	}
}
