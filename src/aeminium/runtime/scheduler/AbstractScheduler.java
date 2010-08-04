package aeminium.runtime.scheduler;

import java.util.Collections;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractScheduler<T extends RuntimeTask> implements RuntimeScheduler<T>, RuntimePrioritizer<T> {
	protected final EnumSet<Flags> flags;
	protected final AtomicInteger runningCount = new AtomicInteger();
	protected final AtomicInteger pausedCount = new AtomicInteger();
	protected RuntimePrioritizer<T> prioritizer = null;
	protected final int maxParallelism = Factory.getParallelism();

	public AbstractScheduler(EnumSet<Flags> flags) {
		this.flags = flags;
	}

	@Override
	public void setPrioritizer(RuntimePrioritizer<T> prioritizer ) {
		this.prioritizer = prioritizer;
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
		scheduleTasks(Collections.singleton(task));
	}
}
