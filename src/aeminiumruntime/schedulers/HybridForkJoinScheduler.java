package aeminiumruntime.schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsr166y.ForkJoinTask;

import aeminiumruntime.BlockingTask;
import aeminiumruntime.RuntimeTask;
import aeminiumruntime.graphs.TaskGraph;

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
                task.call();
                scheduleAllTasks();
            }
        };
    }

	@Override
    public void scheduleTask(RuntimeTask task) {
        if (task instanceof BlockingTask) {
            Logger.getLogger(HybridForkJoinScheduler.class.getName()).log(Level.FINER, "Launching Blocking Task #" + task.getId());
        	iopool.execute(createWorkerTask(task));
        } else {
            Logger.getLogger(HybridForkJoinScheduler.class.getName()).log(Level.FINER, "Launching Computational Task #" + task.getId());
        	ForkJoinTask<Object> fjtask = createThreadFromTask(task);
        	pool.execute(fjtask);
        }
    }
	
}
