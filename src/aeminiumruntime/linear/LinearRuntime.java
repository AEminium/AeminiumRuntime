package aeminiumruntime.linear;

import aeminiumruntime.AtomicTask;
import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;
import aeminiumruntime.NonBlockingTask;
import aeminiumruntime.Runtime;
import aeminiumruntime.RuntimeTask;
import aeminiumruntime.Task;
import aeminiumruntime.schedulers.LinearScheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LinearRuntime extends Runtime {

    private LinearTaskGraph graph;
    private LinearScheduler scheduler;
    private int idCounter;
    
    @Override
    public void init() {
        graph = new LinearTaskGraph();
        scheduler = new LinearScheduler(graph);
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
        return true;
    }

    @Override
    public void shutdown() {
        try {
            scheduler.turnOff();
            scheduler.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(LinearRuntime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public DataGroup createDataGroup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BlockingTask createBlockingTask(Callable<Body> b) {
        try {
            return new LinearBlockingTask(b.call(), idCounter++);
        } catch (Exception ex) {
            Logger.getLogger(LinearRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }

    @Override
    public NonBlockingTask createNonBlockingTask(Body b) {
        try {
            return new LinearNonBlockingTask(b, idCounter++);
        } catch (Exception ex) {
            Logger.getLogger(LinearRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }

    @Override
    public AtomicTask createAtomicTask(Body b, DataGroup g) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
