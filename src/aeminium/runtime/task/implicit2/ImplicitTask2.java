package aeminium.runtime.task.implicit2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.CyclicDependencyError;
import aeminium.runtime.Hints;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.ResultBody;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.AbstractTaskFactory;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.TaskFactory;

public abstract class ImplicitTask2<T extends ImplicitTask2> extends AbstractTask<T> {
	protected ImplicitTaskState2 state = ImplicitTaskState2.UNSCHEDULED;
	protected int depCount = 0;
	protected int childCount = 0;
	protected List<T> dependents = null;
	protected List<T> children;
	protected T parent = null;
	protected RuntimePrioritizer<T> prioritizer = null;
	protected final boolean debug;
	
	public ImplicitTask2( Body body, Collection<Hints> hints, EnumSet<Flags> flags) {
		super(body, hints, flags);
		if ( flags.contains(Flags.DEBUG)) {
			debug = true;
		} else {
			debug = false;
		}
	}

	public static TaskFactory<ImplicitTask2> createFactory(EnumSet<Flags> flags) {
		return new AbstractTaskFactory<ImplicitTask2>(flags) {
			
			@Override 
			public void init() {}
			@Override 
			public void shutdown() {}
			
			@SuppressWarnings("unchecked")
			@Override
			public RuntimeAtomicTask<ImplicitTask2> createAtomicTask(Body body, RuntimeDataGroup<ImplicitTask2> datagroup, Collection<Hints> hints) {
				return new ImplicitAtomicTask2(body, (RuntimeDataGroup<ImplicitTask2>) datagroup, hints, flags);
			}

			@Override
			public BlockingTask createBockingTask(Body body, Collection<Hints> hints) {
				return new ImplicitBlockingTask2(body, hints, flags);
			}

			@Override
			public NonBlockingTask createNonBockingTask(Body body, Collection<Hints> hints) {
				return  new ImplicitNonBlockingTask2(body, hints, flags);
			}
		};
	}
	
	public void setParent(Task parent) {
		if ( parent != Runtime.NO_PARENT ) {
			synchronized (this) {
				setLevel(((T)parent).getLevel()+1);
				this.parent = (T) parent;
				this.parent.attachChild(this);
			}
		}
	}
	
	public void attachChild(T child) {
		synchronized (this) {
			updateChildCount(1);
			if ( debug ) {
				if ( children == null ) {
					children = new ArrayList<T>(10);
				}
				children.add(child);
			}
		}
	}
	
	public void detachChild(T child) {
		synchronized (this) {
			updateChildCount(-1);
			if ( debug ) {
				if ( children == null ) {
					children = new LinkedList<T>();
				}
				children.remove(child);
			}
		}
	}
	
	protected void updateChildCount(int delta ) {
		synchronized (this) {
			childCount += delta;
			if ( childCount == 0 ) {
				if ( state == ImplicitTaskState2.WAITING_FOR_CHILDREN ) {
					taskCompleted();
				}
			}			
		}
	}
	
	public ImplicitTaskState2 getTaskState() {
		synchronized (this) {
			return state;
		}
	}
	
	public int addDependent(T task) {
		synchronized (this) {
			if ( state == ImplicitTaskState2.COMPLETED ) {
				return 0;
			}
			if ( dependents == null ) {
				dependents = new ArrayList<T>(5);
			}
			dependents.add(task);
			return 1;
		}
	}
	
	public void setDependencies(Collection<T> deps) {
		synchronized (this) {
			state = ImplicitTaskState2.WAITING_FOR_DEPENDENCIES;
			if ( (Object)deps != Runtime.NO_DEPS ) {
				int count = 0;
				for ( T t : deps ) {
					synchronized (t) {
						 count = t.addDependent(this);						
					}
				}
				updateDependencyCount(count);
			} else {
				scheduleTask();
			}
		}
	}
	
	public void decDepencenyCount() {
		synchronized (this) {
			updateDependencyCount(-1);
		}
	}
	
	protected void updateDependencyCount(int delta) {
		depCount += delta;
		if ( depCount == 0 ) {
			scheduleTask();
		}			
	}

	protected void scheduleTask() {
		synchronized (this) {
			assert( state == ImplicitTaskState2.WAITING_FOR_DEPENDENCIES );
			state = ImplicitTaskState2.RUNNING;
			prioritizer.scheduleTask((T)this);			
		}
	}
	
	public boolean hasDependecies() {
		synchronized (this) {
			return (depCount != 0);			
		}
	}
	
	public boolean hasChildren() {
		synchronized (this) {
			return (childCount != 0);			
		}
	}
	
	public void taskFinished() {
		synchronized (this) {
			assert( state == ImplicitTaskState2.RUNNING );
			state = ImplicitTaskState2.WAITING_FOR_CHILDREN;
			
			if ( !hasChildren() ) {
				taskCompleted();
			}
		}
	}
	
	@Override
	public void taskCompleted() {
		assert( state == ImplicitTaskState2.WAITING_FOR_CHILDREN );
		state = ImplicitTaskState2.COMPLETED;	

		// callback to ResultBody to compute final result 
		// BEFORE we trigger parent/dependents 
		if ( body instanceof ResultBody<?> ) {
			((ResultBody) body).completed();
		
		
		if ( parent != null) {
			parent.detachChild(this);
		}
		
		if ( dependents != null ) {
			for ( ImplicitTask2<T> t : dependents) {
				t.decDepencenyCount();
			}
		}
		}
		
		// cleanup
		if ( dependents != null ) {
			this.dependents.clear();
			this.dependents = null;
		}
		this.body = null;
		this.parent = null;
		if ( this.children != null ) {
			this.children.clear();
			this.children = null;
		}
	}
	
	public void setPrioritizer(RuntimePrioritizer<T> prioritizer) {
		synchronized (this) {
			this.prioritizer = prioritizer;			
		}
	}
	
	public void checkForCycles() {
		synchronized (this) {
			checkForCycles((T)this, dependents);
		}
	}
	
	protected void checkForCycles(T task, Collection<T> deps) {
		if ( deps == null ) {
			return;
		}
		for ( Task t : deps ) {
			checkPath(task, (T)t);
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
