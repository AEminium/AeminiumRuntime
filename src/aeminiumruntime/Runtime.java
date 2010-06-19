package aeminiumruntime;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

public abstract class Runtime {
    
	@SuppressWarnings("unchecked")
    public final static Collection<Task> NO_DEPS = Collections.EMPTY_LIST;
	@SuppressWarnings("unchecked")
    public final static Collection<Hint> NO_HINTS = Collections.EMPTY_LIST;

    
    /* initialize runtime */
    public abstract void init();
    /* add a task along with it's parent and dependencies */
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