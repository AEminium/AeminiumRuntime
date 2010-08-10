package aeminium.runtime.scheduler.forkjoin;

import java.util.Collection;
import java.util.EnumSet;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinTask;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.taskcounter.RuntimeTaskCounter;

public class ForkJoinScheduler<T extends RuntimeTask> extends AbstractScheduler<T>{
    private ForkJoinPool pool = null;
    
    public ForkJoinScheduler(EnumSet<Flags> flags) {
    	super(flags);
    }
    
    public ForkJoinScheduler(int maxParallelism, EnumSet<Flags> flags) {
		super(maxParallelism, flags);
	}
    
	@Override
    public final void init(RuntimeTaskCounter tc) {
		tc.setPolling();
    	pool = new ForkJoinPool(AeminiumForkJoinWorkerThread.getFactory(tc));
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
    	Thread thread =Thread.currentThread();
    	if (  thread instanceof AeminiumForkJoinWorkerThread ) {
    		AeminiumForkJoinWorkerThread athread = (AeminiumForkJoinWorkerThread)thread;
    		if ( athread.doFork() ) {
    			ForkJoinTask.adapt(task).fork();
    		} else {
    			try {
					task.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
    	} else {
    		pool.execute(ForkJoinTask.adapt(task));
    	}
    }
}
