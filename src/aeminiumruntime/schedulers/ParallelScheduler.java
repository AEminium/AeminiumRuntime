package aeminiumruntime.schedulers;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.graphs.TaskGraph;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelScheduler extends BaseScheduler {

	ExecutorService pool;
	
    public ParallelScheduler(TaskGraph graph) {
        super(graph);
        pool = Executors.newCachedThreadPool();
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
        pool.execute(createWorkerTask(task));
    }
}
