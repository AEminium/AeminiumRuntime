package aeminium.runtime.graph.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import aeminium.runtime.Hint;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.AbstractGraph;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flag;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.RuntimeBlockingTask;
import aeminium.runtime.task.RuntimeNonBlockingTask;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskDescription;
import aeminium.runtime.task.implicit.ImplicitTaskState;

abstract class RuntimeTaskWrapper<T extends RuntimeTask> extends AbstractTask {

	private ImplicitTaskState state = ImplicitTaskState.WAITING_FOR_DEPENDENCIES;
	private Collection<Task> dependencies = Runtime.NO_DEPS;
	
	private Task parent = aeminium.runtime.Runtime.NO_PARENT;
	private int childCount = 0;
	private Collection<Task> dependents = new ArrayList<Task>();
	private Object result;
	protected T task;
	

	public RuntimeTaskWrapper(RuntimeGraph<T> graph, T task) {
		super((RuntimeGraph<RuntimeTask>)graph, task.getBody(), task.getHints());
		this.task = task;
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
	
	public ImplicitTaskState getTaskState() {
			return state;
	}
	
	public void setTaskState(ImplicitTaskState state) {
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

	public Collection<Hint> getHints() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addDependent(T task) {
			dependents.add(task);
	}
	
	public Collection<Task> getDependents(){
		return dependents;
	}

	@Override
	public void setResult(Object value) {
		this.result = value;
	}
	
	@Override
	public Object getResult() {
		return result;
	}
	
	@Override
	public String toString() {
		return "Wrapper<Task<"+body.toString()+">>" + childCount;
	}
 
	@Override
	public Object call() throws Exception {
		task.getBody().execute(task);
		graph.taskFinished(this);
		return null;
	}
	
	public void taskFinished() {
		// nothing by default
	}
	
	public void taskCompleted() {
		// nothing by default
	}
}

class AtomicTaskWrapper<T extends RuntimeAtomicTask> extends RuntimeTaskWrapper<T> implements RuntimeAtomicTask {
	
	public AtomicTaskWrapper(RuntimeGraph<T> graph, T task) {
		super(graph, task);
	}
	
	@Override
	public Object call() throws Exception {
		boolean locked = task.getDataGroup().trylock(this);
		if ( locked ) {
			task.getBody().execute(task);
			graph.taskFinished(this);
		}
		return null;
	}

	@Override 
	public void taskCompleted() {
		task.getDataGroup().unlock();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RuntimeDataGroup<T> getDataGroup() {
		return task.getDataGroup();
	}
}


class BlockingTaskWrapper<T extends RuntimeBlockingTask> extends RuntimeTaskWrapper<T> implements RuntimeBlockingTask {
	
	public BlockingTaskWrapper(RuntimeGraph<T> graph, T task) {
		super(graph, task);
	}
}

class NonBlockingTaskWrapper<T extends RuntimeNonBlockingTask> extends RuntimeTaskWrapper<T> implements RuntimeNonBlockingTask {
	
	public NonBlockingTaskWrapper(RuntimeGraph<T> graph, T task) {
		super(graph, task);
	}
}


public class GenericGraph<T extends RuntimeTask> extends AbstractGraph<T> {
	private final List<RuntimeTaskWrapper<T>>  waitingForDeps= new LinkedList<RuntimeTaskWrapper<T>>();
	private final List<RuntimeTaskWrapper<T>>  running = new LinkedList<RuntimeTaskWrapper<T>>();
	private final List<RuntimeTaskWrapper<T>>  waitingForChildren = new LinkedList<RuntimeTaskWrapper<T>>();
	private final Map<T, RuntimeTaskWrapper<T>> wrapperMapping = new HashMap<T, RuntimeTaskWrapper<T>>();
	
	
	public GenericGraph(EnumSet<Flag> flags, RuntimePrioritizer<T> prioritizer) {
		super(flags, prioritizer);
	}
	
	@Override
	public void init() {
	}
	
	@Override
	public void shutdown() {
	}
	
	@Override
	public void addTask(T task, Task parent, Collection<T> deps) {
		RuntimeTaskWrapper<T> wrapper = null;
		if ( task instanceof RuntimeAtomicTask<?> ) {
			wrapper = new AtomicTaskWrapper(this, (RuntimeAtomicTask) task);
		} else if ( task instanceof RuntimeBlockingTask ) {
			wrapper = new BlockingTaskWrapper(this, (RuntimeBlockingTask) task);
		} else {
			wrapper = new NonBlockingTaskWrapper(this, (RuntimeNonBlockingTask) task);
		}
		wrapperMapping.put(task, wrapper);
		addWrapperTask(wrapper, parent, deps);
	}
	
	protected void addWrapperTask(RuntimeTaskWrapper<T> task, Task parent, Collection<T> deps) {
		// setup dependendecies
		if ( deps != Runtime.NO_DEPS ) {
			task.setDependencies(new ArrayList<Task>(deps));
		} else {
			task.setDependencies(Runtime.NO_DEPS);
		}
		
		if ( parent != Runtime.NO_PARENT ) {
			((RuntimeTaskWrapper<T>)wrapperMapping.get(parent)).addChildTask(task);
			task.setParent(wrapperMapping.get(parent));
		} else {
			task.setParent(Runtime.NO_PARENT);
		}
		
		synchronized (this) {
 			synchronized (task) {
				if ( task.getDependencies() == aeminium.runtime.Runtime.NO_DEPS ) {
					running.add(task);
					task.setTaskState(ImplicitTaskState.RUNNING);
					prioritizer.scheduleTasks((T)task);
				} else {
					List<Task> doneTasks = new ArrayList<Task>();
					for ( Task t : task.getDependencies() ) {
						synchronized (t) {
							RuntimeTaskWrapper<RuntimeTask> at = (RuntimeTaskWrapper<RuntimeTask>)t;
							if ( at.getTaskState() != ImplicitTaskState.FINISHED ) {
								at.addDependent((RuntimeTaskWrapper<RuntimeTask>)task);
							} else {
								doneTasks.add(at);
							}
						}
					}
					task.removeDependency(doneTasks);
					if ( task.getDependencies() != aeminium.runtime.Runtime.NO_DEPS ){
						task.setTaskState(ImplicitTaskState.WAITING_FOR_DEPENDENCIES);
						waitingForDeps.add(task);
					} else {
						running.add(task);
						task.setTaskState(ImplicitTaskState.RUNNING);
						prioritizer.scheduleTasks((T)task);
					}
				}
			}
		}
	}
	
	// task finished to run 
	public void taskFinished(T task_p) {
		RuntimeTaskWrapper<T> task = (RuntimeTaskWrapper<T>)task_p;
		synchronized (this) {
			synchronized (task) {
				running.remove(task);
				// callback 
				task.taskFinished();
				if (task.hasChildren()) {
					waitingForChildren.add(task);
					task.setTaskState(ImplicitTaskState.WAITING_FOR_CHILDREN);
				} else {
					taskCompleted(task);
				}
			}
		}
	}

	// have to synchronize on task and this
	protected void taskCompleted(RuntimeTaskWrapper<T> task) {
		synchronized (this) {
			synchronized (task) {
				if ( task.getTaskState() == ImplicitTaskState.WAITING_FOR_CHILDREN ) {
					waitingForChildren.remove(task);
				}
				task.setTaskState(ImplicitTaskState.FINISHED);
				// callback 
				task.taskCompleted();
				if ( task.getParent() != aeminium.runtime.Runtime.NO_PARENT ) {
					RuntimeTaskWrapper<T> parent = (RuntimeTaskWrapper<T>)task.getParent();
					synchronized (parent) {
						parent.deleteChildTask(task);
						if ( !parent.hasChildren() && parent.getTaskState() == ImplicitTaskState.WAITING_FOR_CHILDREN) {
							taskCompleted(parent);
						}
					}
				}
				List<RuntimeTaskWrapper<T>> readyTasks = new ArrayList<RuntimeTaskWrapper<T>>();
				for ( Task t : task.getDependents() ) {
					synchronized (t) {
						@SuppressWarnings("unchecked")
						RuntimeTaskWrapper<T> at = (RuntimeTaskWrapper<T>)t;
						at.removeDependency(task);
						if ( at.getDependencies() == aeminium.runtime.Runtime.NO_DEPS ) {
							waitingForDeps.remove(at);
							running.add(at);
							at.setTaskState(ImplicitTaskState.RUNNING);
							readyTasks.add(at);
							//prioritizer.scheduleTasks(at);
						}
					}
				}
				if ( !readyTasks.isEmpty() ) {
					prioritizer.scheduleTasks((T[])readyTasks.toArray());
				}

				if (waitingForChildren.isEmpty() && waitingForDeps.isEmpty() && running.isEmpty()) {
					this.notifyAll();
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
		RuntimeTaskWrapper<T> wrapper = (RuntimeTaskWrapper<T>)task;
		return TaskDescription.create(task, wrapper.getDependencies().size(), wrapper.getDependents().size());
	}
}