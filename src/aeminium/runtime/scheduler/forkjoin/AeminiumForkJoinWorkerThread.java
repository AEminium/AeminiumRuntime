package aeminium.runtime.scheduler.forkjoin;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinWorkerThread;
import jsr166y.ForkJoinPool.ForkJoinWorkerThreadFactory;

public class AeminiumForkJoinWorkerThread extends ForkJoinWorkerThread {
	
	public static ForkJoinWorkerThreadFactory getFactory() {
		return new ForkJoinWorkerThreadFactory() {
			@Override
			public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
				AeminiumForkJoinWorkerThread thread = new AeminiumForkJoinWorkerThread(pool);
				return thread;
			}
		};
	}
	
	protected AeminiumForkJoinWorkerThread(ForkJoinPool pool) {
		super(pool);
	}

}
