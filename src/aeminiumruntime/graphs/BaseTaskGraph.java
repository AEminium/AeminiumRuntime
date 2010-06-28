package aeminiumruntime.graphs;

import java.util.ArrayList;
import java.util.List;

import aeminiumruntime.RuntimeTask;

public abstract class BaseTaskGraph implements TaskGraph {

    
    List<RuntimeTask> readyList = new ArrayList<RuntimeTask>();
    List<RuntimeTask> runningList = new ArrayList<RuntimeTask>();
	
    protected abstract void updateGraph();
    
	@Override
    public synchronized boolean hasNext() {
        updateGraph();
        return !readyList.isEmpty();
    }

}
