package aeminiumruntime.simpleparallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import aeminiumruntime.*;
import aeminiumruntime.graphs.DependencyDeadlockException;
import aeminiumruntime.graphs.ParallelTaskGraph;
import aeminiumruntime.schedulers.HybridForkJoinScheduler;
import aeminiumruntime.schedulers.Scheduler;

public class ParallelRuntime extends aeminiumruntime.Runtime {

    private ParallelTaskGraph graph;
    private Scheduler scheduler;
    private int idCounter;
    
    @Override
    public void init() {
        graph = new ParallelTaskGraph();
        scheduler = new HybridForkJoinScheduler(graph);
        idCounter = 0;
    }

    @Override
    public boolean schedule(Task task, Collection<Task> deps) {

        // TODO Stupid casting
        Collection<RuntimeTask> rdeps = new ArrayList<RuntimeTask>();
        if (deps != null) {
            for (Task t: deps) rdeps.add((RuntimeTask) t);
        }

        graph.add((RuntimeTask) task, rdeps);
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
    public BlockingTask createBlockingTask(Body b) {
        try {
            return new ParallelBlockingTask(b, idCounter++);
        } catch (Exception ex) {
            Logger.getLogger(ParallelRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }

    @Override
    public NonBlockingTask createNonBlockingTask(Body b) {
        try {
            return new ParallelNonBlockingTask(b, idCounter++);
        } catch (Exception ex) {
            Logger.getLogger(ParallelRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }

    @Override
    public AtomicTask createAtomicTask(Body b, DataGroup g) {
        try {
            return new ParallelAtomicTask(b, idCounter++, (ParallelDataGroup) g);
        } catch (Exception ex) {
            Logger.getLogger(ParallelRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }

	@Override
	public boolean schedule(Task task, Task parent, Collection<Task> deps) {
		return schedule(task, deps);
	}

}
