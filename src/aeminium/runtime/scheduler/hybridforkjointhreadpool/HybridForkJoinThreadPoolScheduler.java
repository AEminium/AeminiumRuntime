package aeminium.runtime.scheduler.hybridforkjointhreadpool;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinTask;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;

public class HybridForkJoinThreadPoolScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {
	private ExecutorService blockingService;
	private ForkJoinPool fjpool;
	
	public HybridForkJoinThreadPoolScheduler(EnumSet<Flags> flags) {
		super(flags);
        
	}
	
	@Override
	public void init() {
		fjpool = new ForkJoinPool();
        fjpool.setAsyncMode(true);
        blockingService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
	}
	
	@Override
	public void scheduleTasks(Collection<T> tasks) {
		for (T t : tasks) {
			scheduleTask(t);
		}
	}
	
	@Override
	public void scheduleTask(T task) {
		if ( task instanceof NonBlockingTask ) {
			fjpool.execute(ForkJoinTask.adapt((Callable)task));
		} else {
			// IO and blocking guess to thread pool
			blockingService.submit(task);
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
