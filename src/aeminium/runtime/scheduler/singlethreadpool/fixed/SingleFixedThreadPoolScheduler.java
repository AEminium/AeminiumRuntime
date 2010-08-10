package aeminium.runtime.scheduler.singlethreadpool.fixed;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.taskcounter.RuntimeTaskCounter;
import aeminium.runtime.taskcounter.SimpleTaskCountingThread;

public class SingleFixedThreadPoolScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {
	private ExecutorService execService; 
	
	public SingleFixedThreadPoolScheduler(EnumSet<Flags> flags) {
		super(flags);
	}

	public SingleFixedThreadPoolScheduler(int maxParallelism, EnumSet<Flags> flags) {
		super(maxParallelism, flags);
	}

	@Override
	public final void init(RuntimeTaskCounter tc) {
		execService = Executors.newFixedThreadPool(getMaxParallelism(),SimpleTaskCountingThread.getFactory(tc));
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
