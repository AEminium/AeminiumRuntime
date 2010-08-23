package aeminium.runtime.scheduler.forkjoin;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinWorkerThread;
import jsr166y.ForkJoinPool.ForkJoinWorkerThreadFactory;
import aeminium.runtime.implementations.implicitworkstealing.events.RuntimeEventManager;

public class AeminiumForkJoinWorkerThread extends ForkJoinWorkerThread {
	
	public static ForkJoinWorkerThreadFactory getFactory(final RuntimeEventManager eventManager) {
		return new ForkJoinWorkerThreadFactory() {
			@Override
			public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
				AeminiumForkJoinWorkerThread thread = new AeminiumForkJoinWorkerThread(pool);
				eventManager.signalNewThread(thread);
				return thread;
			}
		};
	}
	
	protected AeminiumForkJoinWorkerThread(ForkJoinPool pool) {
		super(pool);
	}

}
