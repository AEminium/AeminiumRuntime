package aeminium.runtime.scheduler.hybridforkjointhreadpool;

import java.util.EnumSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinTask;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.implementations.Flag;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;

public class HybridForkJoinThreadPoolScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {
	private ExecutorService blockingService;
	private ForkJoinPool fjpool;
	
	public HybridForkJoinThreadPoolScheduler(EnumSet<Flag> flags) {
		super(flags);
        
	}
	
	@Override
	public void init() {
		fjpool = new ForkJoinPool();
        fjpool.setAsyncMode(true);
        blockingService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
	}
	
	@Override
	public void scheduleTasks(T... tasks) {
		for ( int i = 0 ; i < tasks.length ; i++ ) {
			if ( tasks[i] instanceof NonBlockingTask ) {
				fjpool.execute(ForkJoinTask.adapt((Callable)tasks[i]));
			} else {
				// IO and blocking guess to thread pool
				blockingService.submit(tasks[i]);
			}
		}
		
	}

	@Override
	public void shutdown() {
		if ( fjpool != null ) {
			fjpool.shutdown();
			fjpool = null;
		}
		if ( blockingService != null ) {
			blockingService.shutdown();
			blockingService = null;
		}
	}
}
