package aeminiumruntime.linear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;

public class LinearTaskGraph implements TaskGraph {
    List<RuntimeTask> readyList = new ArrayList<RuntimeTask>();
    List<RuntimeTask> runningList = new ArrayList<RuntimeTask>();

    public synchronized boolean add(RuntimeTask task, Collection<RuntimeTask> deps) {
        readyList.add(task);
        return true;
    }

    /* Removes finished tasks from dependencies  */
    private synchronized void updateGraph() {
        for (Iterator<RuntimeTask> it = runningList.iterator(); it.hasNext(); ) {
            RuntimeTask t = (RuntimeTask) it.next();
            if (t.isDone()) {
                it.remove();
            }
        }
    }

    public synchronized boolean hasNext() {
        updateGraph();
        return !readyList.isEmpty();
    }

    public synchronized RuntimeTask next() {
        updateGraph();
        RuntimeTask task = readyList.get(0);
        readyList.remove(0);
        runningList.add(task);
        return task;
    }

    public void remove() {}

    public boolean isDone() {
        return runningList.isEmpty() && readyList.isEmpty();
    }
    
    
}
