package aeminium.runtime.graph.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.CyclicDependencyError;
import aeminium.runtime.Hints;
import aeminium.runtime.Runtime;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.Task;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.AbstractGraph;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.RuntimeBlockingTask;
import aeminium.runtime.task.RuntimeNonBlockingTask;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskDescription;
import aeminium.runtime.task.implicit.ImplicitTask;
import aeminium.runtime.task.implicit.ImplicitTaskState;

enum WrapperTaskState {
	UNSCHEDULED,
	WAITING_FOR_DEPENDENCIES,
	RUNNING,
	WAITING_FOR_CHILDREN,
	COMPLETED
}

abstract class RuntimeTaskWrapper<T extends RuntimeTask> extends AbstractTask {

	private WrapperTaskState state = WrapperTaskState.UNSCHEDULED;
	private Collection<Task> dependencies = Runtime.NO_DEPS;
	
	private Task parent = aeminium.runtime.Runtime.NO_PARENT;
	private int childCount = 0;
	private Collection<RuntimeTaskWrapper<T>> dependents = new ArrayList<RuntimeTaskWrapper<T>>();
	protected T task;
	

	public RuntimeTaskWrapper(RuntimeGraph<T> graph, T task, EnumSet<Flags> flags) {
		super((RuntimeGraph<RuntimeTask>)graph, task.getBody(), task.getHints(), flags);
		this.task = task;
	}
	
	public T getTask() {
		return task;
	}

	public void setDependencies(Collection<Task> dependencies) {
		this.dependencies = dependencies;
	}
	
	public void removeDependency(Task task) {
		dependencies.remove(task);
		if ( dependencies.isEmpty()) {
			dependencies = aeminium.runtime.Runtime.NO_DEPS;
		}
	}
	
	public void removeDependency(Collection<Task> tasks) {
		dependencies.removeAll(tasks);
		if ( dependencies.isEmpty()) {
			dependencies = aeminium.runtime.Runtime.NO_DEPS;
		}
	}
	
	public final Collection<Task> getDependencies() {
		return dependencies;
	}
	
	public void setParent(Task parent) {
		this.parent = parent;
	}
	
	public Task getParent() {
		return  parent;
	}
	
	public WrapperTaskState getTaskState() {
			return state;
	}
	
	public void setTaskState(WrapperTaskState state) {
			this.state = state;
	}
	
	public boolean hasChildren() {
		if ( 0 < childCount) {
			return true;
		} else {
			return false;
		}
	}
	
	public void addChildTask(Task child){
		childCount++;
	}
	
	public void deleteChildTask(Task child) {
		childCount--;
	}

	public Collection<Hints> getHints() {
		return Runtime.NO_HINTS;
	}

	public void addDependent(RuntimeTaskWrapper<T> task) {
			dependents.add(task);
	}
	
	public Collection<RuntimeTaskWrapper<T>> getDependents(){
		return dependents;
	}

	
	@Override
	public String toString() {
		return "Wrapper<"+task+">" + childCount;
	}
 
	@Override
	public Object call() throws Exception {
		task.getBody().execute(task);
		graph.taskFinished(this);
		return null;
	}
	
	public void taskCompleted() {
		// nothing by default
	}
}

class AtomicTaskWrapper<T extends RuntimeAtomicTask> extends RuntimeTaskWrapper<T> implements RuntimeAtomicTask {
	
	public AtomicTaskWrapper(RuntimeGraph<T> graph, T task, EnumSet<Flags> flags) {
		super(graph, task, flags);
	}
	
	@Override
	public Object call() throws Exception {
		throw new RuntimeError("RuntimeTaskWrapper should never been executed.");
	}

	@Override 
	public void taskCompleted() {
		throw new RuntimeError("RuntimeTaskWrapper should never been executed.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public RuntimeDataGroup<T> getDataGroup() {
		return task.getDataGroup();
	}
}

class BlockingTaskWrapper<T extends RuntimeBlockingTask> extends RuntimeTaskWrapper<T> implements RuntimeBlockingTask {
	
	public BlockingTaskWrapper(RuntimeGraph<T> graph, T task, EnumSet<Flags> flags) {
		super(graph, task, flags);
	}
}

class NonBlockingTaskWrapper<T extends RuntimeNonBlockingTask> extends RuntimeTaskWrapper<T> implements RuntimeNonBlockingTask {
	
	public NonBlockingTaskWrapper(RuntimeGraph<T> graph, T task, EnumSet<Flags> flags) {
		super(graph, task, flags);
	}
}


public class GenericGraph<T extends RuntimeTask> extends AbstractGraph<T> {
	private final List<RuntimeTaskWrapper<T>>  waitingForDeps= new LinkedList<RuntimeTaskWrapper<T>>();
	private final List<RuntimeTaskWrapper<T>>  running = new LinkedList<RuntimeTaskWrapper<T>>();
	private final List<RuntimeTaskWrapper<T>>  waitingForChildren = new LinkedList<RuntimeTaskWrapper<T>>();
	private final boolean checkForCycles;
	private final RuntimeTask[] rta = new RuntimeTask[0];
	protected final String TASK_KEY = GenericGraph.class.getCanonicalName();
	
	public GenericGraph(RuntimePrioritizer<T> prioritizer, EnumSet<Flags> flags) {
		super(prioritizer, flags);
		if ( flags.contains(Flags.CHECK_FOR_CYCLES)) {
			checkForCycles = true;
		} else {
			checkForCycles = false;
		}
	}
	
	@Override
	public void init() {
	}
	
	@Override
	public void shutdown() {
	}
	
	public RuntimeTaskWrapper<T> wrapTask(T task) {
		RuntimeTaskWrapper<T> wtask;
		
		if ( task instanceof RuntimeNonBlockingTask ) {
			wtask =  new NonBlockingTaskWrapper(this, (RuntimeNonBlockingTask) task, flags);
		} else if ( task instanceof RuntimeBlockingTask ) {
			wtask =  new BlockingTaskWrapper(this, (RuntimeBlockingTask) task, flags);
		} else {
			wtask =  new AtomicTaskWrapper(this, (RuntimeAtomicTask) task, flags);
		}
		
		task.setData(TASK_KEY, wtask);
		return wtask;
	}
	
	@Override
	public void addTask(T task, Task parent, Collection<T> deps) {
		synchronized (this) {
 			synchronized (task) {
 				RuntimeTaskWrapper<T> wtask = (RuntimeTaskWrapper<T>) task.getData(TASK_KEY);
 				if ( wtask == null ) {
 					wtask = wrapTask(task);
 				}
 				
 				// setup dependencies
 				if ( wtask.getTaskState() != WrapperTaskState.UNSCHEDULED ) {
 					throw new RuntimeError("Task '" + task + "' has already been scheduled");
 				}
 				
 				if ( deps != Runtime.NO_DEPS ) {
 					ArrayList<Task> wdeps = new ArrayList<Task>(deps.size());
 					for( T t : deps ) {
 						RuntimeTaskWrapper<T> dep = (RuntimeTaskWrapper<T>) t.getData(TASK_KEY);
 						if ( dep == null ) {
 							dep = wrapTask(t);
 						}
 						wdeps.add(dep);
 					}
 					wtask.setDependencies(wdeps);
 				} else {
 					wtask.setDependencies(Runtime.NO_DEPS);
 				}
 				
 				if ( parent != Runtime.NO_PARENT ) {
 					RuntimeTaskWrapper<T> wparent = (RuntimeTaskWrapper<T>) ((T)parent).getData(TASK_KEY);
 					if ( parent == null) {
 						wparent = wrapTask((T) parent);
 					}
 					wparent.addChildTask(wtask);
 					wtask.setParent(wparent);
 				} else {
 					wtask.setParent(Runtime.NO_PARENT);
 				}
 				
 				if ( checkForCycles ) {
 					Collection<Task> taskDeps = Collections.unmodifiableList((List<? extends Task>) wtask.getDependencies());
 					checkForCycles(wtask, taskDeps);
 				}
 				
 				
 				if ( wtask.getDependencies() == Runtime.NO_DEPS ) {
					running.add(wtask);
					wtask.setTaskState(WrapperTaskState.RUNNING);
					prioritizer.scheduleTasks(wtask.getTask());
				} else {
					List<Task> doneTasks = new ArrayList<Task>();
					for ( Task t : wtask.getDependencies() ) {
						synchronized (t) {
							RuntimeTaskWrapper<T> at = (RuntimeTaskWrapper<T>)t;
							if ( at.getTaskState() != WrapperTaskState.COMPLETED ) {
								at.addDependent(wtask);
							} else {
								doneTasks.add(at);
							}
						}
					}
					wtask.removeDependency(doneTasks);
					if ( wtask.getDependencies() != aeminium.runtime.Runtime.NO_DEPS ){
						wtask.setTaskState(WrapperTaskState.WAITING_FOR_DEPENDENCIES);
						waitingForDeps.add(wtask);
					} else {
						running.add(wtask);
						wtask.setTaskState(WrapperTaskState.RUNNING);
						prioritizer.scheduleTasks(wtask.getTask());
					}
				}
			}
		}
	}
	
	protected void checkForCycles(RuntimeTaskWrapper<T> task, Collection<Task> deps) {
		if ( deps == Runtime.NO_DEPS ) {
			return;
		}
		for ( Task t : deps ) {
			checkPath(task, (RuntimeTaskWrapper<T>)t);
		}
	}
	
	protected void checkPath(RuntimeTaskWrapper<T> task, RuntimeTaskWrapper<T> dep) {
		if ( task == dep ) {
			throw new CyclicDependencyError("Found Cycle for task: " + task);
		} else {
			Collection<Task> nextDeps;
			synchronized (dep) {
				 nextDeps = Collections.unmodifiableList((List<? extends Task>) dep.getDependencies());
			}
			checkForCycles(task, nextDeps);
		}
	}
	
	// task finished to run 
	public void taskFinished(T task) {
		RuntimeTaskWrapper<T> wtask = (RuntimeTaskWrapper<T>) task.getData(TASK_KEY);
		synchronized (this) { 
			synchronized (task) {
				running.remove(wtask);
				if (wtask.hasChildren()) {
					waitingForChildren.add(wtask);
					wtask.setTaskState(WrapperTaskState.WAITING_FOR_CHILDREN);
				} else {
					taskCompleted(wtask);
				}
			}
		}
	}

	// have to synchronize on task and this
	protected void taskCompleted(RuntimeTaskWrapper<T> wtask) {
		synchronized (this) {
			synchronized (wtask) {
				if ( wtask.getTaskState() == WrapperTaskState.WAITING_FOR_CHILDREN ) {
					waitingForChildren.remove(wtask);
				}
				wtask.setTaskState(WrapperTaskState.COMPLETED);
				// callback 
				wtask.getTask().taskCompleted();
				if ( wtask.getParent() != Runtime.NO_PARENT ) {
					@SuppressWarnings("unchecked")
					RuntimeTaskWrapper<T> wparent = (RuntimeTaskWrapper<T>)wtask.getParent();
					synchronized (wparent) {
						wparent.deleteChildTask(wtask);
						if ( !wparent.hasChildren() && wparent.getTaskState() == WrapperTaskState.WAITING_FOR_CHILDREN) {
							taskCompleted(wparent);
						}
					}
				}
				ArrayList<T> readyTasks = new ArrayList<T>(10);
				for ( Task t : wtask.getDependents() ) {
					synchronized (t) {
						@SuppressWarnings("unchecked")
						RuntimeTaskWrapper<T> wt = (RuntimeTaskWrapper<T>)t;
						wt.removeDependency(wtask);
						if ( wt.getDependencies() == Runtime.NO_DEPS ) {
							waitingForDeps.remove(wt);
							running.add(wt);
							wt.setTaskState(WrapperTaskState.RUNNING);
							//prioritizer.scheduleTasks(at);
							readyTasks.add(wt.getTask());
						}
					}
				}

				// trigger prioritize in case he was caching some tasks
				prioritizer.scheduleTasks((T[]) readyTasks.toArray(rta));
				
				// wake up waiting threads 
				if (waitingForChildren.isEmpty() && waitingForDeps.isEmpty() && running.isEmpty()) {
					this.notifyAll();
				} else {
//					if ( waitingForChildren.isEmpty() == false ) {
//						System.out.println("waitingForChildren " + waitingForChildren);
//					}
//					if ( waitingForDeps.isEmpty() == false ) {
//						System.out.println("waitingForDeps " + waitingForDeps);
//					}
//					if ( running.isEmpty() == false ) {
//						System.out.println("running " + running);
//					}
				}
			}
		}
	}
	
	public void waitToEmpty() {
		synchronized (this) {
			while ( !(waitingForChildren.isEmpty() && waitingForDeps.isEmpty() && running.isEmpty())) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public TaskDescription<T> getTaskDescription(T task) {
		RuntimeTaskWrapper<T> wtask = (RuntimeTaskWrapper<T>)task.getData(TASK_KEY);
		if ( wtask == null ) {
			throw new RuntimeError("Cannot get task description for a task that has not been scheduled.");
		}
		return TaskDescription.create(task, wtask.getDependencies().size(), wtask.getDependents().size());
	}
}