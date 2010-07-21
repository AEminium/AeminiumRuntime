package aeminium.runtime.scheduler.hybridthreadpools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public class HybridThreadPoolsScheduler<T extends RuntimeTask> implements RuntimeScheduler<T>, RuntimePrioritizer<T> {
	private ExecutorService blockingService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*10);
	private ExecutorService nonblockingService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	@Override
	public void scheduleTasks(T ... tasks) {
		if ( tasks.length == 1 ) {
			// optimize case of single
			scheduleTask(tasks[0]);
		} else {
			for ( int i = 0; i < tasks.length; i++ ) {
				scheduleTask(tasks[i]);
			}
		}
	}

	protected void scheduleTask(T task) {
		if ( task instanceof NonBlockingTask ) {
			nonblockingService.submit(task);
		} else {
			blockingService.submit(task);
		}		
	}

	@Override
	public void shutdown() {
		blockingService.shutdown();
		nonblockingService.shutdown();		
	}

}

