package aeminiumruntime.schedulers;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;


public class LinearScheduler extends BaseScheduler {

    public LinearScheduler(TaskGraph graph) {
        super(graph);
    }

    @Override
    public void scheduleWork() {
        synchronized (graph) {
            if (graph.hasNext()) {
                // Get Next
                RuntimeTask task = (RuntimeTask) graph.next();
                task.execute();
            }
        }
    }
}
