package aeminiumruntime;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Runtime {
    /* global constants used for the */
    public final static Collection<Task> NO_DEPS = new ArrayList<Task>() {
		private static final long serialVersionUID = 1852797887380877437L;

		@Override 
		public String toString() {
			return "NO_DEPS"; 
		}
	};
    public final static Collection<Hint> NO_HINTS = new ArrayList<Hint>() {
		private static final long serialVersionUID = -4192191055485289203L;

		@Override 
		public String toString() {
			return "NO_HINTS"; 
		}
	};
    public final static Task NO_PARENT = new Task() {
		@Override
		public Collection<Hint> getHints() {
			return null;
		}
		
		@Override
		public void setResult(Object value) {
			
		}

		@Override
		public Object getResult() {
			return null;
		}
		@Override
		public String toString() {
			return "NO_PARENT";
		}
	};
	
	
    protected boolean debug = false;
    /* turn debug mode on */
    public void startDebug() {
    	debug = true;
    }
    
    /* initialize runtime */
    public abstract void init();
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