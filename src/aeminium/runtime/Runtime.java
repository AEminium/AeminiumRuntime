package aeminium.runtime;

import java.util.ArrayList;
import java.util.Collection;

public interface Runtime {
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
	
    /* initialize runtime */
    public void init() throws RuntimeError;
    /* add a task along with it's parent and dependencies */
    public void schedule(Task task, Task parent, Collection<Task> deps) throws RuntimeError;
    /* returns the current task object */
    public void shutdown() throws RuntimeError;

    /* create a new data group object */
    public DataGroup createDataGroup() throws RuntimeError;
    /* create a new Blocking task */
    public BlockingTask createBlockingTask(Body b, Collection<Hint> hints) throws RuntimeError;
    /* create a new NonBlocking task */
    public NonBlockingTask createNonBlockingTask(Body b, Collection<Hint> hints) throws RuntimeError;
    /* create a new Atomic task */
    public AtomicTask createAtomicTask(Body b, DataGroup g, Collection<Hint> hints) throws RuntimeError;
}