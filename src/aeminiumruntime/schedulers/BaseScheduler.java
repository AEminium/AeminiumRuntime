package aeminiumruntime.schedulers;

import aeminiumruntime.TaskGraph;

public abstract class BaseScheduler extends Thread implements Scheduler {

    TaskGraph graph;
    Boolean isOn;

    public BaseScheduler(TaskGraph graph) {
        this.graph = graph;
        isOn = true;
    }

    public synchronized void turnOff() {
        isOn = false;
    }
    
    
    public abstract void scheduleWork();
    

    @Override
    public void run() {

        while (hasWorkLeft()) {
            scheduleWork();
        }
    }
    
    private boolean hasWorkLeft() {
        synchronized (isOn) {
            if (isOn) return true;
        }
        synchronized (graph) {
            if (!graph.isDone()) return true;
        }
        return false;
    }
    
    public void refresh() {
    	this.interrupt();
    }
    
}
