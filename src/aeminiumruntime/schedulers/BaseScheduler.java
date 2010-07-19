package aeminiumruntime.schedulers;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.graphs.TaskGraph;

public abstract class BaseScheduler implements Scheduler {

    TaskGraph graph;

    public BaseScheduler(TaskGraph graph) {
        this.graph = graph;
    }

    public void scheduleAllTasks() {
    	while(hasTasksToRun()) {
    		RuntimeTask t = getNextTask();
    		if (t != null) {
    			scheduleTask(t);
    		}
    	}
    }

	public abstract void scheduleTask(RuntimeTask task);
    
    
	/* Methods below are not inlined together not to cause deadlocks on the graph */
	
    private boolean hasTasksToRun() {
    	synchronized (graph) {
    		return graph.hasNext();
    	}	
    }
    
    private RuntimeTask getNextTask() {
    	synchronized (graph) {
    		return graph.next();
    	}
	}
    
}
