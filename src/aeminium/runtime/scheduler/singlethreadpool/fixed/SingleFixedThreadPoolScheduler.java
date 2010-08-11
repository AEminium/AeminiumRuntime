package aeminium.runtime.scheduler.singlethreadpool.fixed;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;

public class SingleFixedThreadPoolScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {
	private ExecutorService execService; 
	
	public SingleFixedThreadPoolScheduler() {
		super();
	}

	public SingleFixedThreadPoolScheduler(int maxParallelism) {
		super(maxParallelism);
	}

	@Override
	public final void init(RuntimeEventManager eventManager) {
		execService = Executors.newFixedThreadPool(getMaxParallelism());
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
