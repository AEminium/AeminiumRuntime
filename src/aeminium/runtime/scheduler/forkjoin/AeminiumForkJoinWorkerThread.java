package aeminium.runtime.scheduler.forkjoin;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinWorkerThread;
import jsr166y.ForkJoinPool.ForkJoinWorkerThreadFactory;

public class AeminiumForkJoinWorkerThread extends ForkJoinWorkerThread {
	protected int counter = 0;	
	
	public static ForkJoinWorkerThreadFactory getFactory() {
		return new ForkJoinWorkerThreadFactory() {
			@Override
			public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
				return new AeminiumForkJoinWorkerThread(pool);
			}
		};
	}
	
	protected AeminiumForkJoinWorkerThread(ForkJoinPool pool) {
		super(pool);
	}

	protected final boolean doFork() {
		return (counter++ %3 == 0);
	}
}
