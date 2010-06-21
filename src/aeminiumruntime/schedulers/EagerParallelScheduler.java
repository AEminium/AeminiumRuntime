package aeminiumruntime.schedulers;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;

public class EagerParallelScheduler extends BaseScheduler {

    public EagerParallelScheduler(TaskGraph graph) {
        super(graph);
    }

    private Thread createWorkerThread(final RuntimeTask task) {
        return new Thread() {
            @Override
            public void run() {
                task.call();
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
