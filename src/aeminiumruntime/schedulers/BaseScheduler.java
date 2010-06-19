package aeminiumruntime.schedulers;

import aeminiumruntime.TaskGraph;

public abstract class BaseScheduler implements Scheduler {

    TaskGraph graph;

    public BaseScheduler(TaskGraph graph) {
        this.graph = graph;
    }

    public void refresh() {
    	while (hasWorkLeft()) {
    		scheduleWork();
    	}
    }
    
    public abstract void scheduleWork();
    
    private boolean hasWorkLeft() {
        synchronized (graph) {
            if (!graph.isDone()) return true;
        }
        return false;
    }
    
}
