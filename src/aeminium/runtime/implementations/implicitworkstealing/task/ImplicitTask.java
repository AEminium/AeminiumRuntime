package aeminium.runtime.implementations.implicitworkstealing.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import aeminium.runtime.Body;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.error.ErrorManager;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;


public abstract class ImplicitTask implements Task {
	protected static final Object UNSET = new Object() {
		@Override
		public String toString() {
			return "UNSET";
		}
	};
	protected volatile Object result = UNSET;  // could merge result with body  
	public Body body;
	public ImplicitTaskState state = ImplicitTaskState.UNSCHEDULED;  // could be a byte instead of a reference
	public byte depCount;
	public byte childCount;
	public List<ImplicitTask> dependents;  
	public List<ImplicitTask> children;     // children are only used for debugging purposes => could be removed
	public ImplicitTask parent;
	public static final boolean debug = Configuration.getProperty(ImplicitTask.class, "debug", false);
	public final short hints;
	public short level;
	public Thread waiter;    // we could same this and just mention that there is someone waiting

	public ImplicitTask(Body body, short hints) {
		this.body = body;
		this.hints = hints;
	}
		
	public void invoke(ImplicitWorkStealingRuntime rt) {
		try {
			body.execute(rt, this);
		} catch (Throwable e) {
			rt.getErrorManager().signalTaskException(this, e);
			setResult(e);
		} finally {
			taskFinished(rt);
		}
	}

	@Override
	public final void setResult(Object result) {
		if ( result == null ) {
			//throw new RuntimeError("Cannot set result to 'null'.");
		}
		this.result = result;
	}
	
	@Override
	public final Object getResult() {
		if ( isCompleted() ) {
			return result;
		} else {
			Thread thread = Thread.currentThread();
			if ( thread instanceof WorkStealingThread ) {
				((WorkStealingThread)thread).progressToCompletion(this);
			} else {
				synchronized (this) {
					while ( !isCompleted() ) {
						waiter = thread;
						try {
							this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return result;
		}
	}
	
	public final void attachChild(ImplicitWorkStealingRuntime rt, ImplicitTask child) {
		synchronized (this) {
			childCount += 1;
			if ( debug ) {
				if ( children == null ) {
					children = new ArrayList<ImplicitTask>(10);
				}
				children.add(child);
			}
		}
	}
	
	public final void detachChild(ImplicitWorkStealingRuntime rt, ImplicitTask child) {
		synchronized (this) {
			childCount -= 1;
			if ( childCount == 0 ) {
				if ( state == ImplicitTaskState.WAITING_FOR_CHILDREN ) {
					taskCompleted(rt);
				}
			}
		}
	}

	public final int addDependent(ImplicitTask task) {
		synchronized (this) {
			if ( state == ImplicitTaskState.COMPLETED ) {
				return 0;
			}
			if ( dependents == null ) {
				dependents = new ArrayList<ImplicitTask>();
			}
			dependents.add(task);
			return 1;
		}
	}
	
	public final void decDependencyCount(ImplicitWorkStealingRuntime rt) {
		boolean schedule = false;
		synchronized (this) {
			depCount -= 1;
			if ( depCount == 0 ) {
				state = ImplicitTaskState.RUNNING;
				schedule = true;
			}
		}
		if ( schedule ) {
			rt.scheduler.scheduleTask(this);	
		}
	}
	
	public final void taskFinished(ImplicitWorkStealingRuntime rt) {
		synchronized (this) {
			state = ImplicitTaskState.WAITING_FOR_CHILDREN;

			if ( childCount == 0 ) {
				taskCompleted(rt);
			}
		}
	}
	
	public void taskCompleted(ImplicitWorkStealingRuntime rt) {
		assert( state == ImplicitTaskState.WAITING_FOR_CHILDREN );
		state = ImplicitTaskState.COMPLETED;	

		if ( parent != null) {
			parent.detachChild(rt, this);
			this.parent = null;
		}

		if ( dependents != null ) {
			for ( ImplicitTask t : dependents) {
				t.decDependencyCount(rt);
			}
			this.dependents = null;
		}

		// cleanup references 
		this.body = null;
		this.children = null;
		
		rt.graph.taskCompleted(this);
		
		if ( waiter != null ) {
			notifyAll();
		}
	}

	public final boolean isCompleted() {
		return state == ImplicitTaskState.COMPLETED;
	}
	
	public void checkForCycles(final ErrorManager em) {
		synchronized (this) {
			checkForCycles(this, dependents, em);
		}
	}
	
	protected void checkForCycles(final ImplicitTask task, final Collection<ImplicitTask> deps, final ErrorManager em) {
		if ( deps == null ) {
			return;
		}
		for ( ImplicitTask t : deps ) {
			checkPath(task, t, em);
		}
	}
	
	protected void checkPath(final ImplicitTask task, ImplicitTask dep, final ErrorManager em) {
		if ( task == dep ) {
			em.singalDependencyCycle(task);
		} else {
			Collection<ImplicitTask> nextDependents;
			synchronized (dep) {
				 nextDependents = Collections.unmodifiableList((List<? extends ImplicitTask>) dep.dependents);
			}
			checkForCycles(task, nextDependents, em);
		}
	}
	
	@Override
	public String toString() {
		return "Task<"+body+">[children:"+childCount+", deps:"+depCount+", state:"+state+"]";
	}
}
