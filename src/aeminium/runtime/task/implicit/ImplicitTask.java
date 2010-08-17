package aeminium.runtime.task.implicit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.CyclicDependencyError;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.Task;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.implementations.AbstractRuntime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.AbstractTaskFactory;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.TaskFactory;


public abstract class ImplicitTask<T extends ImplicitTask<T>> extends AbstractTask<T> {
	public ImplicitTaskState state = ImplicitTaskState.UNSCHEDULED;  // could be a byte instead of a reference
	public byte depCount;
	public byte childCount;
	public List<T> dependents;  
	public List<T> children;     // children are only used for debugging purposes => could be removed
	public T parent;
	public static final boolean debug = Configuration.getProperty(ImplicitTask.class, "debug", false);
	
	public ImplicitTask(Body body, short hints) {
		super(body, hints);
	}

	@SuppressWarnings("unchecked")
	public static TaskFactory<ImplicitTask> createFactory() {
		return new AbstractTaskFactory<ImplicitTask>() {
			
			@Override 
			public final void init() {}
			@Override 
			public final void shutdown() {}
			
			@Override
			public final RuntimeAtomicTask createAtomicTask(Body body, RuntimeDataGroup<ImplicitTask> datagroup, short hints) {
				return new ImplicitAtomicTask(body, (RuntimeDataGroup<ImplicitTask>) datagroup, hints);
			}

			@Override
			public final BlockingTask createBlockingTask(Body body, short hints) {
				return new ImplicitBlockingTask(body, hints);
			}

			@Override
			public final NonBlockingTask createNonBlockingTask(Body body, short hints) {
				return  new ImplicitNonBlockingTask(body, hints);
			}
		};
	}
	
	public final void attachChild(T child) {
		//synchronized (this) {
			childCount += 1;
			if ( childCount == 0 ) {
				if ( state == ImplicitTaskState.WAITING_FOR_CHILDREN ) {
					taskCompleted();
				}
			}
			if ( debug ) {
				if ( children == null ) {
					children = new ArrayList<T>(10);
				}
				children.add(child);
			}
		//}
	}
	
	public final void detachChild(T child) {
		synchronized (this) {
			childCount -= 1;
			if ( childCount == 0 ) {
				if ( state == ImplicitTaskState.WAITING_FOR_CHILDREN ) {
					taskCompleted();
				}
			}
		}
	}

	public final int addDependent(T task) {
		synchronized (this) {
			if ( state == ImplicitTaskState.COMPLETED ) {
				return 0;
			}
			if ( dependents == null ) {
				dependents = new ArrayList<T>();
			}
			dependents.add(task);
			return 1;
		}
	}
	
	public final void decDepencenyCount() {
		boolean schedule = false;
		synchronized (this) {
			depCount -= 1;
			if ( depCount == 0 ) {
				state = ImplicitTaskState.RUNNING;
				schedule = true;
			}
		}
		if ( schedule ) {
			@SuppressWarnings("unchecked")
			T This = (T)this;
			((RuntimePrioritizer<T>)AbstractRuntime.prioritizer).scheduleTask(This);	
		}
	}
	
	public final void taskFinished() {
		synchronized (this) {
			state = ImplicitTaskState.WAITING_FOR_CHILDREN;

			if ( childCount == 0 ) {
				taskCompleted();
			}
		}
	}
	
	@Override
	public void taskCompleted() {
		assert( state == ImplicitTaskState.WAITING_FOR_CHILDREN );
		state = ImplicitTaskState.COMPLETED;	

		if ( parent != null) {
			@SuppressWarnings("unchecked")
			T Tthis = (T)this;
			parent.detachChild(Tthis);
			this.parent = null;
		}

		if ( dependents != null ) {
			for ( ImplicitTask<T> t : dependents) {
				t.decDepencenyCount();
			}
			this.dependents = null;
		}

		// cleanup references 
		this.body = null;
		this.children = null;
		
		AbstractRuntime.graph.taskCompleted((T)this);
		
		if ( waiter != null ) {
			notifyAll();
		}
	}

	public final boolean isCompleted() {
		return state == ImplicitTaskState.COMPLETED;
	}
	
	public void checkForCycles() {
		synchronized (this) {
			@SuppressWarnings("unchecked")
			T Tthis = (T)this;
			checkForCycles(Tthis, dependents);
		}
	}
	
	protected void checkForCycles(T task, Collection<T> deps) {
		if ( deps == null ) {
			return;
		}
		for ( Task t : deps ) {
			@SuppressWarnings("unchecked")
			T Tt = (T)t;
			checkPath(task, Tt);
		}
	}
	
	protected void checkPath(T task, T dep) {
		if ( task == dep ) {
			throw new CyclicDependencyError("Found Cycle for task: " + task);
		} else {
			Collection<T> nextDependents;
			synchronized (dep) {
				 nextDependents = Collections.unmodifiableList((List<? extends T>) dep.dependents);
			}
			checkForCycles(task, (Collection<T>)nextDependents);
		}
	}
	
	@Override
	public String toString() {
		return "Task<"+body+">[children:"+childCount+", deps:"+depCount+", state:"+state+"]";
	}
}
