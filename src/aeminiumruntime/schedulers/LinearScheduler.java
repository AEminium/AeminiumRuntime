package aeminiumruntime.schedulers;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;


public class LinearScheduler extends BaseScheduler {

    public LinearScheduler(TaskGraph graph) {
        super(graph);
    }

    @Override
    public void scheduleWork() {
        boolean willWait = false;
        synchronized (graph) {
            if (graph.hasNext()) {
                // Get Next
                RuntimeTask task = (RuntimeTask) graph.next();
                task.execute();
                
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
