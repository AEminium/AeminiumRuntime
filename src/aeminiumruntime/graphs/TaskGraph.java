package aeminiumruntime.graphs;

import java.util.Collection;
import java.util.Iterator;

import aeminiumruntime.RuntimeTask;

public interface TaskGraph extends Iterator<RuntimeTask> {
	public boolean add(RuntimeTask task, RuntimeTask parent, Collection<RuntimeTask> deps);
    public boolean isDone();
    
    public void checkForCycles(RuntimeTask t)
	throws DependencyDeadlockException;
}