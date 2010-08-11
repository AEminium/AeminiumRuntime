package aeminium.runtime.scheduler.singlethreadpool.cached;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;

public class SingleCachedThreadPoolScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {
	private ExecutorService execService; 
	
	public SingleCachedThreadPoolScheduler() {
		super();
	}

	public SingleCachedThreadPoolScheduler(int maxParallelism) {
		super(maxParallelism);
	}
	
	@Override
	public final void init(RuntimeEventManager eventManager) {
		execService = Executors.newCachedThreadPool();
		eventManager.signalPolling();
	}

	@Override
	public final void shutdown() {
		if ( execService != null ) {
			execService.shutdown();
			execService = null;
		}
	}

	@Override
	public final void scheduleTasks(Collection<T> tasks) {
		for ( T t : tasks ) {
			scheduleTask(t);
		}
	}
	
	public final void scheduleTask(T task) {
		runningCount.incrementAndGet();
		task.setScheduler(this);
		execService.submit(task);
	}

}
