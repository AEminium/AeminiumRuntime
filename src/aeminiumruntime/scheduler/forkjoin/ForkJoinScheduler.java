package aeminiumruntime.scheduler.forkjoin;

import java.util.EnumSet;
import java.util.concurrent.Callable;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinTask;
import aeminiumruntime.implementations.Flag;
import aeminiumruntime.scheduler.AbstractScheduler;
import aeminiumruntime.task.RuntimeTask;

public class ForkJoinScheduler<T extends RuntimeTask> extends AbstractScheduler<T>{

    private final ForkJoinPool pool;
    
    public ForkJoinScheduler(EnumSet<Flag> flags) {
    	super(flags);
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
	
    public synchronized void shutdown() {
    	pool.shutdown();
    }



    
}
