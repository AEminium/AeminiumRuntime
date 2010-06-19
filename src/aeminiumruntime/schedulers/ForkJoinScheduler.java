package aeminiumruntime.schedulers;

import java.util.concurrent.Callable;

import jsr166y.ForkJoinPool;
import jsr166y.ForkJoinTask;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;

public class ForkJoinScheduler extends BaseScheduler {

    ForkJoinPool pool;
    
    public ForkJoinScheduler(TaskGraph graph) {
        super(graph);
        pool = new ForkJoinPool();
        pool.setAsyncMode(true);
    }

    @Override
    public void scheduleTask(RuntimeTask task) {
    	ForkJoinTask<Object> thread = createThreadFromTask(task);
        pool.execute(thread);
    }

    protected ForkJoinTask<Object> createThreadFromTask(final RuntimeTask task) {
        Callable<Object> threadWrapper = new Callable<Object>() {
            public Object call() {
                Object result = task.execute();
                scheduleAllTasks();
                return result;
            }
        };
        ForkJoinTask<Object> thread = ForkJoinTask.adapt(threadWrapper);
        return thread;
    }

    
    
}
