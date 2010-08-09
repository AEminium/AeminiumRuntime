package aeminium.runtime.scheduler.forkjoin;

import java.util.Collection;
import java.util.EnumSet;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinTask;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;

public class ForkJoinScheduler<T extends RuntimeTask> extends AbstractScheduler<T>{

    private ForkJoinPool pool = null;
    
    public ForkJoinScheduler(EnumSet<Flags> flags) {
    	super(flags);
    }
    
    public ForkJoinScheduler(int maxParallelism, EnumSet<Flags> flags) {
		super(maxParallelism, flags);
	}
    
	@Override
    public final void init() {
    	pool = new ForkJoinPool(AeminiumForkJoinWorkerThread.getFactory());
    }
    
	@Override
    public final void shutdown() {
		if ( pool != null ) {
			pool.shutdown();
			pool = null;
		}
	}

	@Override
    public final void scheduleTasks(Collection<T> tasks) {
    	for ( T t : tasks ) {
    		scheduleTask(t);
    	}
    }
	
    @Override
    public final void scheduleTask(T task) {
    	task.setScheduler(this);
    	if ( Thread.currentThread() instanceof AeminiumForkJoinWorkerThread ) {
    		ForkJoinTask.adapt(task).fork();
    	} else {
    		pool.execute(ForkJoinTask.adapt(task));
    	}
    }
}
