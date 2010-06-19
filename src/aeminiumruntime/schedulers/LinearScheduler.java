package aeminiumruntime.schedulers;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;


public class LinearScheduler extends Thread {

    TaskGraph graph;
    Boolean isOn;

    public LinearScheduler(TaskGraph graph) {
        this.graph = graph;
        isOn = true;
    }

    public synchronized void turnOff() {
        isOn = false;
    }

    @Override
    public void run() {
        boolean willWait = false;
        
        while (hasWorkLeft()) {
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
    
    private boolean hasWorkLeft() {
        synchronized (isOn) {
            if (isOn) return true;
        }
        synchronized (graph) {
            if (!graph.isDone()) return true;
        }
        return false;
    }
}
