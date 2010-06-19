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
            }
        };
    }

    @Override
    public void scheduleWork() {
        boolean willWait = false;
        
        synchronized (graph) {
            if (graph.hasNext()) {

                Thread taskThread = createWorkerThread((RuntimeTask) graph.next());
                taskThread.start();
            } else {
                willWait = true;
            }
        }
        if (willWait) {
            try {
                // Wait for other threads to execute;
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                // Get back to work, you lazy scheduler!
            }
        }
        
    }
}
