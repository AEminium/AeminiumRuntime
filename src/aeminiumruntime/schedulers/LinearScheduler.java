package aeminiumruntime.schedulers;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.graphs.TaskGraph;


public class LinearScheduler extends BaseScheduler {

    public LinearScheduler(TaskGraph graph) {
        super(graph);
    }

    @Override
    public void scheduleTask(RuntimeTask task) {
        task.call();
    }
}
