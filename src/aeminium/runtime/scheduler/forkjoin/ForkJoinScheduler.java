package aeminium.runtime.scheduler.forkjoin;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.Callable;

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

    @Override
    public void init() {
    	pool = new ForkJoinPool();
        pool.setAsyncMode(true);
    }
    
    @Override
    public void scheduleTasks(Collection<T> tasks) {
    	for ( T t : tasks ) {
    		scheduleTask(t);
    	}
    }
	
    @Override
    @SuppressWarnings("unchecked")
    public void scheduleTask(T task) {
    	pool.execute(ForkJoinTask.adapt((Callable)task));
    }
    
	public void shutdown() {
		if ( pool != null ) {
			pool.shutdown();
			pool = null;
		}
    }



    
}
