package aeminium.runtime.scheduler.forkjoin;

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
	public void scheduleTasks(T... tasks) {
		if ( tasks.length == 1 ) {
			pool.execute(ForkJoinTask.adapt((Callable)tasks[0]));
		} else {
			for ( int i = 0 ; i < tasks.length ; i++ ) {
				pool.execute(ForkJoinTask.adapt((Callable)tasks[i]));
			}
		}
		
	}
	
	public void shutdown() {
		if ( pool != null ) {
			pool.shutdown();
			pool = null;
		}
    }



    
}
