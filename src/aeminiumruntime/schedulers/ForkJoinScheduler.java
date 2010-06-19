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
    }

    @Override
    public void scheduleWork() {
        boolean willWait = false;
        
        synchronized (graph) {
            if (graph.hasNext()) {
                RuntimeTask task = (RuntimeTask) graph.next();
                ForkJoinTask<Object> thread = createThreadFromTask(task);
                pool.execute(thread);
            } else {
                willWait = true;
            }
        }
        if (willWait) {
            try {
                // Wait for other threads to execute;
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                // Get back to work, you lazy scheduler!
            }
        }
        
    }

    protected ForkJoinTask<Object> createThreadFromTask(final RuntimeTask task) {
        Callable<Object> threadWrapper = new Callable<Object>() {
            public Object call() {
                return task.execute();
            }
        };
        ForkJoinTask<Object> thread = ForkJoinTask.adapt(threadWrapper);
        return thread;
    }

    
    
}
