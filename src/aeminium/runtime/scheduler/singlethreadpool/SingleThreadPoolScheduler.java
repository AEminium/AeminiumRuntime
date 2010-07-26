package aeminium.runtime.scheduler.singlethreadpool;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;

public class SingleThreadPoolScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {
	private ExecutorService execService; 
	
	public SingleThreadPoolScheduler(EnumSet<Flags> flags) {
		super(flags);
	}

	@Override
	public void init() {
		execService = Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors()));
	}

	@Override
	public void scheduleTasks(T... tasks) {
		try {
			execService.invokeAll(Arrays.asList(tasks));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shutdown() {
		if ( execService != null ) {
			execService.shutdown();
			execService = null;
		}
	}

}
