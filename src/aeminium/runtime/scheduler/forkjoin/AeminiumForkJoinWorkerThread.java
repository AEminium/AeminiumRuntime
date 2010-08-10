package aeminium.runtime.scheduler.forkjoin;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinWorkerThread;
import jsr166y.ForkJoinPool.ForkJoinWorkerThreadFactory;
import aeminium.runtime.taskcounter.RuntimeTaskCounter;
import aeminium.runtime.taskcounter.TaskCountingThread;

public class AeminiumForkJoinWorkerThread extends ForkJoinWorkerThread implements TaskCountingThread {
	protected int counter = 0;
	protected volatile long taskCount = 0; 
	
	public static ForkJoinWorkerThreadFactory getFactory(final RuntimeTaskCounter taskCounter) {
		return new ForkJoinWorkerThreadFactory() {
			@Override
			public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
				AeminiumForkJoinWorkerThread thread = new AeminiumForkJoinWorkerThread(pool);
				taskCounter.registerThread(thread);
				return thread;
			}
		};
	}
	
	protected AeminiumForkJoinWorkerThread(ForkJoinPool pool) {
		super(pool);
	}

	protected final boolean doFork() {
		return (counter++ %3 == 0);
	}

	@Override
	public long getDelta() {
		return taskCount;
	}

	@Override
	public void tasksAdded(int delta) {
		taskCount += delta;
	}

	@Override
	public void tasksCompleted(int delta) {
		taskCount -= delta;
	}
}
