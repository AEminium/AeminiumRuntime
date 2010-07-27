package aeminium.runtime.task.implicit2;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.CyclicDependencyError;
import aeminium.runtime.Hints;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.Runtime;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.Task;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.AbstractTaskFactory;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.TaskFactory;

public class ImplicitTask2<T extends ImplicitTask2> extends AbstractTask<T> {
	protected final ReentrantLock lock = new ReentrantLock();
	protected final AtomicReference<ImplicitTaskState2> state = new AtomicReference<ImplicitTaskState2>(ImplicitTaskState2.UNSCHEDULED);
	protected final AtomicInteger depCount = new AtomicInteger(0);
	protected final AtomicInteger childCount = new AtomicInteger(0);
	protected Collection<T> dependents = null;
	protected T parent = null;
	protected final ImplicitTask2<T>[] ita = new ImplicitTask2[0];
	protected RuntimePrioritizer<T> prioritizer = null;
	protected final boolean debug;
	protected List<T> children;
	
	public ImplicitTask2(RuntimeGraph<T> graph, Body body,
			Collection<Hints> hints, EnumSet<Flags> flags) {
		super(graph, body, hints, flags);
		if ( flags.contains(Flags.DEBUG)) {
			debug = true;
		} else {
			debug = false;
		}
	}

	public static TaskFactory<ImplicitTask2> createFactory(final RuntimeGraph<ImplicitTask2> graph, EnumSet<Flags> flags) {
		return new AbstractTaskFactory<ImplicitTask2>(flags) {
			@Override 
			public void init() {}
			@Override 
			public void shutdown() {}
			
			@SuppressWarnings("unchecked")
			@Override
			public RuntimeAtomicTask<ImplicitTask2> createAtomicTask(Body body, RuntimeDataGroup<ImplicitTask2> datagroup, Collection<Hints> hints) {
				return new ImplicitAtomicTask2((RuntimeGraph<ImplicitTask2>) graph, body, (RuntimeDataGroup<ImplicitTask2>) datagroup, hints, flags);
			}

			@Override
			public BlockingTask createBockingTask(Body body, Collection<Hints> hints) {
				return new ImplicitBlockingTask2((RuntimeGraph<ImplicitTask2>) graph, body, hints, flags);
			}

			@Override
			public NonBlockingTask createNonBockingTask(Body body, Collection<Hints> hints) {
				return  new ImplicitNonBlockingTask2((RuntimeGraph<ImplicitTask2>) graph, body, hints, flags);
			}
		};
	}
	
	public void lock() {
		lock.lock();
	}
	
	public void unlock() {
		lock.unlock();
	}
	
	public void setParent(Task parent) {
		if ( parent != Runtime.NO_PARENT ) {
			this.parent = (T) parent;
			this.parent.attachChild(this);
		}
	}
	
	public void attachChild(T child) {
		updateChildCount(1);
		if ( debug ) {
			lock.lock();
			if ( children == null ) {
				children = new LinkedList<T>();
			}
			children.add(child);
			lock.unlock();
		}
	}
	
	public void detachChild(T child) {
		updateChildCount(-1);
		if ( debug ) {
			lock.lock();
			if ( children == null ) {
				children = new LinkedList<T>();
			}
			children.remove(child);
			lock.unlock();
		}
	}
	
	protected void updateChildCount(int delta ) {
		int value = childCount.addAndGet(delta);
		if ( value == 0 ) {
			if ( state.get() == ImplicitTaskState2.WAITING_FOR_CHILDREN ) {
				taskCompleted();
			}
		}
	}
	
	public ImplicitTaskState2 getTaskState() {
		return state.get();
	}
	
	public void setTaskState(ImplicitTaskState2 state) {
		this.state.set(state);
	}
	
	public int addDependent(T task) {
		lock.lock();
		if ( state.get() == ImplicitTaskState2.COMPLETED ) {
			lock.unlock();
			return 0;
		}
		if ( dependents == null ) {
			dependents = new LinkedList<T>();
		}
		dependents.add(task);
		lock.unlock();
		return 1;
	}
	
	public void setDependencies(Collection<T> deps) {
		state.set(ImplicitTaskState2.WAITING_FOR_DEPENDENCIES);
		if ( (Object)deps != Runtime.NO_DEPS ) {
			for ( T t : deps ) {
				updateDependencyCount(t.addDependent(this));
			};
		} else {
			scheduleTask();
		}
	}
	
	public void decDepencenyCount() {
		updateDependencyCount(-1);
	}
	
	protected void updateDependencyCount(int delta) {
		int value = depCount.addAndGet(delta);
		if ( value == 0 ) {
			scheduleTask();
		}
	}

	protected void scheduleTask() {
		if ( !state.compareAndSet(ImplicitTaskState2.WAITING_FOR_DEPENDENCIES, ImplicitTaskState2.RUNNING)) {
			return;
		} 
		prioritizer.scheduleTasks((T)this);
	}
	
	public boolean hasDependecies() {
		return (depCount.get() != 0);
	}
	
	public boolean hasChildren() {
		return (childCount.get() != 0);
	}
	
	public void taskFinished() {
		//System.out.println("task finished " + this);
		if ( !state.compareAndSet(ImplicitTaskState2.RUNNING, ImplicitTaskState2.WAITING_FOR_CHILDREN) ) {
			throw new RuntimeError("Finished task is not in RUNNING state.");
		}
		if ( !hasChildren() ) {
			taskCompleted();
		}
	}
	
	@Override
	public void taskCompleted() {
		state.set(ImplicitTaskState2.COMPLETED);
		if ( parent != null) {
			parent.detachChild(this);
		}
		lock.lock();
		if ( dependents != null ) {
			for ( ImplicitTask2<T> t : dependents.toArray(ita)) {
				t.decDepencenyCount();
			}
		}
		lock.unlock();
	}
	
	public void setPrioritizer(RuntimePrioritizer<T> prioritizer) {
		this.prioritizer = prioritizer;
	}
	
	public void checkForCycles() {
		checkForCycles((T)this, dependents);
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
		return "Task<"+body.toString()+">[children:"+childCount+", deps:"+depCount+", state:"+state+"]";
	}
}
