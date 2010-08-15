package aeminium.runtime.prioritizer;

import aeminium.runtime.implementations.AbstractRuntime;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractPrioritizer<T extends RuntimeTask> implements RuntimePrioritizer<T> {
	protected final RuntimeScheduler<T> scheduler;
	
	public AbstractPrioritizer(RuntimeScheduler<T> scheduler) {
		this.scheduler = scheduler;
	}
	
	public void init() {
		AbstractRuntime.prioritizer = this;
	}
	
	@Override 
	public void taskFinished(T task) {
	}
	
	@Override 
	public void taskPaused(T task) {
		
	}
}
