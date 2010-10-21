package aeminium.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

public interface Runtime {
    /* global constants used for the */
    public final static Collection<Task> NO_DEPS = new ArrayList<Task>() {
		private static final long serialVersionUID = 1852797887380877437L;

		@Override 
		public String toString() {
			return "NO_DEPS"; 
		}
	};
    public final static short NO_HINTS = Hints.NO_HINTS;
    public final static Task NO_PARENT = new Task() {
		@Override
		public void setResult(Object value) {
			throw new RuntimeError("Cannot set result on NO_PARENT");
		}

		@Override
		public Object getResult() {
			throw new RuntimeError("Cannot get result from NO_PARENT");
		}
		
		@Override
		public String toString() {
			return "NO_PARENT";
		}
	};
	
    /* initialize runtime */
    public void init() throws RuntimeError;
    /* returns the current task object */
    public void shutdown() throws RuntimeError;
    /* add a task along with it's parent and dependencies */
    public void schedule(Task task, Task parent, Collection<Task> deps) throws RuntimeError;


    /* create a new data group object */
    public DataGroup createDataGroup() throws RuntimeError;
    /* create a new Blocking task */
    public BlockingTask createBlockingTask(Body b, short hints) throws RuntimeError;
    /* create a new NonBlocking task */
    public NonBlockingTask createNonBlockingTask(Body b, short hints) throws RuntimeError;
    /* create a new Atomic task */
    public AtomicTask createAtomicTask(Body b, DataGroup g, short hints) throws RuntimeError;

    /* return executor service abstraction for this runtime object */
    public ExecutorService getExecutorService();
}
