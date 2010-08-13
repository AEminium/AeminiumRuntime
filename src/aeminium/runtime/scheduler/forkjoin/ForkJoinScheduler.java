package aeminium.runtime.scheduler.forkjoin;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinTask;
import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;

public class ForkJoinScheduler<T extends RuntimeTask> extends AbstractScheduler<T>{
    private ForkJoinPool pool = null;
    
    public ForkJoinScheduler() {
    	super();
    }
    
    public ForkJoinScheduler(int maxParallelism) {
		super(maxParallelism);
	}
    
	@Override
    public final void init(RuntimeEventManager eventManager) {
    	pool = new ForkJoinPool(AeminiumForkJoinWorkerThread.getFactory());
    	eventManager.signalPolling();
    }
    
	@Override
    public final void shutdown() {
		if ( pool != null ) {
			pool.shutdown();
			pool = null;
		}
	}

    @Override
    public final void scheduleTask(T task) {
    	task.setScheduler(this);
    	Thread thread =Thread.currentThread();
    	if (  thread instanceof AeminiumForkJoinWorkerThread ) {
    		ForkJoinTask.adapt(task).fork();
    	} else {
    		pool.execute(ForkJoinTask.adapt(task));
    	}
    }
}
