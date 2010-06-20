package aeminiumruntime;

import java.util.Collection;
import java.util.Collections;

public abstract class Runtime {
    /* global constants used for the */
	@SuppressWarnings("unchecked")
    public final static Collection<Task> NO_DEPS = Collections.EMPTY_LIST;
	@SuppressWarnings("unchecked")
    public final static Collection<Hint> NO_HINTS = Collections.EMPTY_LIST;
    public final static Task NO_PARENT = new Task() {
		@Override
		public Collection<Hint> getHints() {
			return null;
		}
	};
	
	
    protected boolean debug = false;
    /* turn debug mode on */
    public void startDebug() {
    	debug = true;
    }
    
    /* initialize runtime */
    public abstract void init();
    /* add a task along with it's dependencies */
    @Deprecated
    public abstract boolean schedule(Task task, Collection<Task> deps);
    /* add a task along with it's parent and dependencies */
    public abstract boolean schedule(Task task, Task parent, Collection<Task> deps);
    /* returns the current task object */
    public abstract void shutdown();

    /* create a new data group object */
    public abstract DataGroup createDataGroup();
    /* create a new Blocking task */
    public abstract BlockingTask createBlockingTask(Body b);
    /* create a new NonBlocking task */
    public abstract NonBlockingTask createNonBlockingTask(Body b);
    /* create a new Atomic task */
    public abstract AtomicTask createAtomicTask(Body b, DataGroup g);
}