package aeminiumruntime.schedulers;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;

public class ParallelScheduler extends Thread {

    TaskGraph graph;
    Boolean isOn;

    public ParallelScheduler(TaskGraph graph) {
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
                    Thread taskThread = createWorkerThread((RuntimeTask) graph.next());
                    taskThread.setPriority(Thread.MIN_PRIORITY);
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
    
    private boolean hasWorkLeft() {
        synchronized (isOn) {
            if (isOn) return true;
        }
        synchronized (graph) {
            if (!graph.isDone()) return true;
        }
        return false;
    }
    

    private Thread createWorkerThread(final RuntimeTask task) {
        return new Thread() {
            @Override
            public void run() {
                task.execute();
            }
        };
    }
}
