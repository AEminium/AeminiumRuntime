package aeminium.runtime.scheduler.forkjoin;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinWorkerThread;
import jsr166y.ForkJoinPool.ForkJoinWorkerThreadFactory;

public class AeminiumForkJoinWorkerThread extends ForkJoinWorkerThread {
	
	public static ForkJoinWorkerThreadFactory getFactory() {
		return new ForkJoinWorkerThreadFactory() {
			@Override
			public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
				return new AeminiumForkJoinWorkerThread(pool);
			}
		};
	}
	
	protected AeminiumForkJoinWorkerThread(ForkJoinPool pool) {
		super(pool);
	}
}
