package aeminiumruntime.simpleparallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import aeminiumruntime.AtomicTask;
import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;
import aeminiumruntime.NonBlockingTask;
import aeminiumruntime.Runtime;
import aeminiumruntime.RuntimeTask;
import aeminiumruntime.Task;
import aeminiumruntime.simpleparallel.scheduler.ParallelScheduler;

public class ParallelRuntime extends Runtime {

    private ParallelTaskGraph graph;
    private ParallelScheduler scheduler;
    private int idCounter;
    
    @Override
    public void init() {
        graph = new ParallelTaskGraph();
        scheduler = new ParallelScheduler(graph);
        scheduler.start();
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
        // TODO scheduler.interrupt();

        return true;
    }

    @Override
    public void shutdown() {
        try {
            scheduler.turnOff();
            scheduler.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ParallelRuntime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public DataGroup createDataGroup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BlockingTask createBlockingTask(Callable<Body> b) {
        try {
            return new ParallelBlockingTask(b.call(), idCounter++);
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
