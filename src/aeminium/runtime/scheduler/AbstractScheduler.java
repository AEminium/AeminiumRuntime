package aeminium.runtime.scheduler;

import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.implementations.AbstractRuntime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.implicit.ImplicitTask;

public abstract class AbstractScheduler<T extends RuntimeTask> implements RuntimeScheduler<T>, RuntimePrioritizer<T> {
	protected final AtomicInteger runningCount = new AtomicInteger();
	protected final AtomicInteger pausedCount = new AtomicInteger();
	protected RuntimePrioritizer<T> prioritizer = null;
	protected final int maxParallelism;
	
	public AbstractScheduler() {
		this.maxParallelism = Configuration.getProcessorCount();
	}

	public AbstractScheduler(int maxParallelism) {
		this.maxParallelism = maxParallelism;
	}
	
	public void init () {
    	if ( AbstractRuntime.prioritizer == null ) {
    		AbstractRuntime.prioritizer = this;
    	}
    	AbstractRuntime.scheduler = this;
	}
	
	@Override 
	public int getMaxParallelism() {
		return maxParallelism;
	}
	
	@Override
	public int getRunningTasks() {
		return runningCount.get();
	}
	
	@Override 
	public int getPausedTasks() {
		return pausedCount.get();
	}
	
	@Override
	public void taskFinished(T task) {
		runningCount.decrementAndGet();
		RuntimePrioritizer<T> p = prioritizer;
		if ( p != null ) {
			p.taskFinished(task);
		}
	}
	
	@Override 
	public void taskPaused(T task) {
		pausedCount.incrementAndGet();
		runningCount.decrementAndGet();
		RuntimePrioritizer<T> p = prioritizer;
		if ( p != null ) {
			p.taskPaused(task);
		}
	}
	
	@Override 
	public void taskResume(T task) {
		pausedCount.decrementAndGet();
		// runningCount must be incremented by the schedule function
		scheduleTask(task);
	}
}
