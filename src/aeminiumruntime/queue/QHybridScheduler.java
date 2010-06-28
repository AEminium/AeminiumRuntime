package aeminiumruntime.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aeminiumruntime.BlockingTask;
import aeminiumruntime.NonBlockingTask;

public class QHybridScheduler implements QScheduler {
	private ExecutorService blockingService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
	private ExecutorService nonblockingService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	@Override
	public void schedule(QAbstractTask task) {
		if ( task instanceof BlockingTask ) {
			blockingService.submit(task);
		} else if ( task instanceof NonBlockingTask ) {
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
