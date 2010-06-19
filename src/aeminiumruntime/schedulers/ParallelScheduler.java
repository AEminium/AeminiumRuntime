package aeminiumruntime.schedulers;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;

public class ParallelScheduler extends BaseScheduler {

    public ParallelScheduler(TaskGraph graph) {
        super(graph);
    }

    private Thread createWorkerThread(final RuntimeTask task) {
        return new Thread() {
            @Override
            public void run() {
                task.execute();
                scheduleAllTasks();
            }
        };
    }

    @Override
    public void scheduleTask(RuntimeTask task) {
        Thread taskThread = createWorkerThread(task);
        taskThread.start();  
    }
}
