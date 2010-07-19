package aeminiumruntime.simpleparallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import aeminiumruntime.*;
import aeminiumruntime.graphs.DependencyDeadlockException;
import aeminiumruntime.graphs.ParallelTaskGraph;
import aeminiumruntime.graphs.TaskGraph;
import aeminiumruntime.schedulers.HybridForkJoinScheduler;
import aeminiumruntime.schedulers.Scheduler;

public class ParallelRuntime extends aeminiumruntime.Runtime {

    private TaskGraph graph;
    private Scheduler scheduler;
    private int idCounter;
    
    
    public ParallelRuntime(boolean debug) {
    	if (debug) {
    		startDebug();
    	}
    }
    
    @Override
    public void init() {
        idCounter = 0;
        if (graph == null) {
        	graph = new ParallelTaskGraph();    	
        }
        if (scheduler == null) {
        	scheduler = new HybridForkJoinScheduler(graph);	
        }
        
    }
    
    public TaskGraph getGraph() {
    	return graph;
    }

    public void setGraph(TaskGraph graph) {
    	this.graph = graph;
    }
    
    public void setScheduler(Scheduler s) {
    	this.scheduler = s;
    }

    @Override
    public boolean schedule(Task task, Task parent, Collection<Task> deps) {

        // TODO Stupid casting
        Collection<RuntimeTask> rdeps = new ArrayList<RuntimeTask>();
        if (deps != null) {
            for (Task t: deps) rdeps.add((RuntimeTask) t);
        }
        
        RuntimeTask rparent = null;
        if (parent instanceof RuntimeTask) {
        	rparent = (RuntimeTask) parent;
        }

        graph.add((RuntimeTask) task, rparent, rdeps);
        if (debug) {
        	try {
				graph.checkForCycles((RuntimeTask) task);
			} catch (DependencyDeadlockException e) {
				e.printStackTrace();
				System.exit(1);
			}
        }
        scheduler.scheduleAllTasks();
        
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public DataGroup createDataGroup() {
        return new ParallelDataGroup();
    }

    @Override
    public BlockingTask createBlockingTask(Body b, Collection<Hint> hints) {
        try {
            return new ParallelBlockingTask(b, idCounter++);
        } catch (Exception ex) {
            Logger.getLogger(ParallelRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }

    @Override
    public NonBlockingTask createNonBlockingTask(Body b, Collection<Hint> hints) {
        try {
            return new ParallelNonBlockingTask(b, idCounter++);
        } catch (Exception ex) {
            Logger.getLogger(ParallelRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }

    @Override
    public AtomicTask createAtomicTask(Body b, DataGroup g, Collection<Hint> hints) {
        try {
            return new ParallelAtomicTask(b, idCounter++, (ParallelDataGroup) g);
        } catch (Exception ex) {
            Logger.getLogger(ParallelRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }
}