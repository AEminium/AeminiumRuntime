package aeminium.runtime.task.implicit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.CyclicDependencyError;
import aeminium.runtime.Hints;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.ResultBody;
import aeminium.runtime.Runtime;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.Task;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.AbstractTaskFactory;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskFactory;


public abstract class ImplicitTask<T extends ImplicitTask<T>> extends AbstractTask<T> {
	protected ImplicitTaskState state = ImplicitTaskState.UNSCHEDULED;
	protected int depCount;
	protected int childCount;
	protected List<T> dependents;
	protected List<T> children;
	protected T parent;
	protected RuntimePrioritizer<T> prioritizer;
	protected static final boolean debug = Configuration.getProperty(ImplicitTask.class, "debug", false);
	
	public ImplicitTask(Body body, Collection<Hints> hints) {
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
			public final RuntimeAtomicTask createAtomicTask(Body body, RuntimeDataGroup<ImplicitTask> datagroup, Collection<Hints> hints) {
				return new ImplicitAtomicTask(body, (RuntimeDataGroup<ImplicitTask>) datagroup, hints);
			}

			@Override
			public final BlockingTask createBlockingTask(Body body, Collection<Hints> hints) {
				return new ImplicitBlockingTask(body, hints);
			}

			@Override
			public final NonBlockingTask createNonBlockingTask(Body body, Collection<Hints> hints) {
				return  new ImplicitNonBlockingTask(body, hints);
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	public final void init(Task parent, RuntimePrioritizer<RuntimeTask> prioritizer, RuntimeGraph<RuntimeTask> graph, Collection<RuntimeTask> deps){
		boolean schedule = false;
		synchronized (this) {
			// check for double scheduling
			if ( state != ImplicitTaskState.UNSCHEDULED) {
				throw new RuntimeError("Cannot schedule task twice: " + this);
			}
			
			// setup parent connection
			if ( parent != Runtime.NO_PARENT ) {
				this.parent = (T) parent;
				this.parent.attachChild((T)this);
			}
			
			// initialize references
			this.prioritizer = (RuntimePrioritizer<T>) prioritizer;
			this.graph = (RuntimeGraph<T>) graph;
			
			// setup dependencies
			state = ImplicitTaskState.WAITING_FOR_DEPENDENCIES;
			if ( (Object)deps != Runtime.NO_DEPS ) {
				int count = 0;
				for ( RuntimeTask t : deps) {
					count += ((ImplicitTask<T>) t).addDependent((T)this);						
				}
				depCount += count;
				if ( depCount == 0 ) {
					schedule = true;
				}
			} else {
				schedule = true;
			}
		}
		if (schedule) {
			state = ImplicitTaskState.RUNNING;
			prioritizer.scheduleTask((T)this);	
		}
	}
	
	public final void computeLevel() {
		setLevel(((T)parent).getLevel()+1);
	}
	
	public final void attachChild(T child) {
		synchronized (this) {
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
		}
	}
	
	public final void detachChild(T child) {
		synchronized (this) {
			childCount -= 1;
			if ( childCount == 0 ) {
				if ( state == ImplicitTaskState.WAITING_FOR_CHILDREN ) {
					taskCompleted();
				}
			}
//			if ( debug ) {
//				if ( children != null ) {
//					children.remove(child);
//				}
//			}
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
				schedule = true;
			}
		}
		if ( schedule ) {
			state = ImplicitTaskState.RUNNING;
			@SuppressWarnings("unchecked")
			T This = (T)this;
			prioritizer.scheduleTask(This);	
		}
	}
	
	public final void taskFinished() {
		synchronized (this) {
			assert( state == ImplicitTaskState.RUNNING );
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

		// callback to ResultBody to compute final result 
		// BEFORE we trigger parent/dependents 
		if ( body instanceof ResultBody) {
			((ResultBody) body).completed();
		}

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
		
		@SuppressWarnings("unchecked")
		T This = (T)this;
		graph.taskCompleted(This);	
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
