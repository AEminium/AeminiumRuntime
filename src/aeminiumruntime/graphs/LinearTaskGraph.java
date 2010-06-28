package aeminiumruntime.graphs;

import java.util.Collection;
import java.util.Iterator;

import aeminiumruntime.RuntimeTask;

public class LinearTaskGraph extends BaseTaskGraph {

    public synchronized boolean add(RuntimeTask task, RuntimeTask parent, Collection<RuntimeTask> deps) {
        readyList.add(task);
        return true;
    }

    /* Removes finished tasks from dependencies  */
    protected synchronized void updateGraph() {
        for (Iterator<RuntimeTask> it = runningList.iterator(); it.hasNext(); ) {
            RuntimeTask t = (RuntimeTask) it.next();
            if (t.isDone()) {
                it.remove();
            }
        }
    }

    public synchronized RuntimeTask next() {
        RuntimeTask task = readyList.get(0);
        readyList.remove(0);
        runningList.add(task);
        return task;
    }

    public void remove() {}

    public boolean isDone() {
        return runningList.isEmpty() && readyList.isEmpty();
    }
    
    @Override
    public void checkForCycles(RuntimeTask t)
	throws DependencyDeadlockException {
    	
    }
}
