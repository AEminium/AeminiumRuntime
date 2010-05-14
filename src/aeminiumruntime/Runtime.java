package aeminiumruntime;

import java.util.Collection;
import java.util.concurrent.Callable;

public abstract class Runtime {
    
    /* TODO: fix later
    public ﬁnal static Collection<Task> NO_DEPS;
    public ﬁnal static Collection<Hint> NO_HINTS;
    */

    
    /* initalize runtime */
    public abstract void init();
    /* add a task along with it’s parent and dependencies */
    public abstract boolean schedule(Task task, Collection<Task> deps);
    /* returns the current task object */
    public abstract void shutdown();

    /* create a new data group object */
    public abstract DataGroup createDataGroup();
    /* create a new Blocking task */
    public abstract BlockingTask createBlockingTask(Callable<Body> b);
    /* create a new NonBlocking task */
    public abstract NonBlockingTask createNonBlockingTask(Body b);
    /* create a new Atomic task */
    public abstract AtomicTask createAtomicTask(Body b, DataGroup g);
}