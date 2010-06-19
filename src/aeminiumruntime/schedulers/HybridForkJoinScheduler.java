package aeminiumruntime.schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jsr166y.ForkJoinTask;

import aeminiumruntime.BlockingTask;
import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;

public class HybridForkJoinScheduler extends ForkJoinScheduler {

	ExecutorService iopool;
	
	public HybridForkJoinScheduler(TaskGraph graph) {
		super(graph);
		pool.setParallelism(Runtime.getRuntime().availableProcessors()-1);
		iopool = Executors.newFixedThreadPool(1);
	}
	
    private Runnable createWorkerTask(final RuntimeTask task) {
        return new Runnable() {
            @Override
            public void run() {
                task.execute();
                refresh();
            }
        };
    }

	@Override
    public void scheduleWork() {
        synchronized (graph) {
            if (graph.hasNext()) {
                RuntimeTask task = (RuntimeTask) graph.next();
                if (task instanceof BlockingTask) {
                	System.out.println("Blocked");
                	iopool.execute(createWorkerTask(task));
                } else {
                	System.out.println("Computational");
                	ForkJoinTask<Object> fjtask = createThreadFromTask(task);
                	pool.execute(fjtask);
                }
            }
        }
    }
	
}
