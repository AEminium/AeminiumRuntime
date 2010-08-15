package aeminium.runtime.scheduler.singlethreadpool.fixed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.scheduler.AeminiumThread;
import aeminium.runtime.task.RuntimeTask;

public class SingleFixedThreadPoolScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {
	protected ExecutorService execService; 
	protected AtomicInteger counter = new AtomicInteger();
	
	public SingleFixedThreadPoolScheduler() {
		super();
	}

	public SingleFixedThreadPoolScheduler(int maxParallelism) {
		super(maxParallelism);
	}

	@Override
	public final void init(RuntimeEventManager eventManager) {
		super.init();
		execService = Executors.newFixedThreadPool(getMaxParallelism(), AeminiumThread.getFactory(eventManager));
		eventManager.signalPolling();
	}

	@Override
	public final void shutdown() {
		if ( execService != null ) {
			execService.shutdown();
			execService = null;
		}
	}
	
	public final void scheduleTask(T task) {
		counter.incrementAndGet();
		runningCount.incrementAndGet();
		execService.submit(task);
	}

}
