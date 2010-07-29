package aeminium.runtime.scheduler.singlethreadpool;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aeminium.runtime.RuntimeError;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.implicit2.ImplicitTask2;
import aeminium.runtime.task.implicit2.ImplicitTaskState2;

public class SingleThreadPoolScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {
	private ExecutorService execService; 
	
	public SingleThreadPoolScheduler(EnumSet<Flags> flags) {
		super(flags);
	}

	@Override
	public int getMaxParallelism() {
		return (int)(super.getMaxParallelism() * 1);
	}
	
	@Override
	public void init() {
		execService = Executors.newFixedThreadPool(getMaxParallelism());
	}

	@Override
	public void scheduleTasks(Collection<T> tasks) {
		for ( T t : tasks ) {
			scheduleTask(t);
		}
	}
	
	public void scheduleTask(T task) {
		runningCount.incrementAndGet();
		task.setScheduler(this);
		execService.submit(task);
	}

	@Override
	public void shutdown() {
		if ( execService != null ) {
			execService.shutdown();
			execService = null;
		}
	}

}
