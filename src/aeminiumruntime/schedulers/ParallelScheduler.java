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
                refresh();
            }
        };
    }

    @Override
    public void scheduleWork() {
        synchronized (graph) {
            if (graph.hasNext()) {
                Thread taskThread = createWorkerThread((RuntimeTask) graph.next());
                taskThread.start();
            }
        }
        
    }
}
