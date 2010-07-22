package aeminium.runtime.graph.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import aeminium.runtime.CyclicDependencyError;
import aeminium.runtime.Hints;
import aeminium.runtime.Runtime;
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

enum WrapperTaskState {
	WAITING_FOR_DEPENDENCIES,
	RUNNING,
	WAITING_FOR_CHILDREN,
	FINISHED
}

abstract class RuntimeTaskWrapper<T extends RuntimeTask> extends AbstractTask {

	private WrapperTaskState state = WrapperTaskState.WAITING_FOR_DEPENDENCIES;
	private Collection<Task> dependencies = Runtime.NO_DEPS;
	
	private Task parent = aeminium.runtime.Runtime.NO_PARENT;
	private int childCount = 0;
	private Collection<RuntimeTaskWrapper<T>> dependents = new ArrayList<RuntimeTaskWrapper<T>>();
	private Object result;
	protected T task;
	

	public RuntimeTaskWrapper(RuntimeGraph<T> graph, T task) {
		super((RuntimeGraph<RuntimeTask>)graph, task.getBody(), task.getHints());
		this.task = task;
		task.setData(GenericGraph.TASK_DATA_KEY, this);
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
		// TODO Auto-generated method stub
		return null;
	}

	public void addDependent(RuntimeTaskWrapper<T> task) {
			dependents.add(task);
	}
	
	public Collection<RuntimeTaskWrapper<T>> getDependents(){
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
	private final boolean checkForCycles;
	private final RuntimeTask[] rta = new RuntimeTask[0];
	private Logger log = Logger.getLogger(GenericGraph.class.getCanonicalName());
	public final static String TASK_DATA_KEY = GenericGraph.class.getCanonicalName();
	
	public GenericGraph(EnumSet<Flags> flags, RuntimePrioritizer<T> prioritizer) {
		super(flags, prioritizer);
		if ( flags.contains(Flags.CHECK_FOR_CYCLES)) {
			checkForCycles = true;
		} else {
			checkForCycles = false;
		}
		
		log.setLevel(Level.OFF);
		if (flags.contains(Flags.DEBUG)) {
			log.setLevel(Level.WARNING);
		}
		if ( flags.contains(Flags.TRACE)) {
			log.setLevel(Level.ALL);
		}
		Handler conHdlr = new ConsoleHandler();
		conHdlr.setFormatter(new Formatter() {
			public String format(LogRecord record) {
				return record.getLevel() + "  :  "
				+ record.getMessage() + "\n";
			}
		});
		log.setUseParentHandlers(false);
		log.addHandler(conHdlr);
	}
	
	@Override
	public void init() {
	}
	
	@Override
	public void shutdown() {
	}
	
	@Override
	public void addTask(T task, Task parent, Collection<T> deps) {
		createWrapperMapping(task);
		addWrapperTask(wrapperMapping.get(task), parent, deps);
	}
	
	protected RuntimeTaskWrapper<T> wrapTask(T task) {
		if ( task instanceof RuntimeAtomicTask<?> ) {
			return new AtomicTaskWrapper(this, (RuntimeAtomicTask) task);
		} else if ( task instanceof RuntimeBlockingTask ) {
			return new BlockingTaskWrapper(this, (RuntimeBlockingTask) task);
		} else {
			return new NonBlockingTaskWrapper(this, (RuntimeNonBlockingTask) task);
		}
	}
	
	protected void createWrapperMapping(T t) {
		synchronized (wrapperMapping) {
			if ( !wrapperMapping.containsKey(t)) {
				wrapperMapping.put(t, wrapTask(t));
				log.info("add wrapper for ==> " + t);
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
	
	protected void addWrapperTask(RuntimeTaskWrapper<T> task, Task parent, Collection<T> deps) {
		synchronized (this) {
 			synchronized (task) {
 				// setup dependendecies
 				if ( deps != Runtime.NO_DEPS ) {
 					Collection<Task> dep = new ArrayList<Task>(deps.size());
 					for ( T t : deps ) {
 						createWrapperMapping(t);
 						dep.add(wrapperMapping.get(t));
 					}
 					task.setDependencies(dep);
 				} else {
 					task.setDependencies(Runtime.NO_DEPS);
 				}
 				
 				if ( parent != Runtime.NO_PARENT ) { 					
 					createWrapperMapping((T)parent);
 					task.setParent(wrapperMapping.get(parent));
 					wrapperMapping.get(parent).addChildTask(task);
 				} else {
 					task.setParent(Runtime.NO_PARENT);
 				}
 				
 				if ( checkForCycles ) {
 					Collection<Task> taskDeps = Collections.unmodifiableList((List<? extends Task>) task.getDependencies());
 					checkForCycles(task, taskDeps);
 				}
 				
				if ( task.getDependencies() == aeminium.runtime.Runtime.NO_DEPS ) {
					running.add(task);
					task.setTaskState(WrapperTaskState.RUNNING);
					prioritizer.scheduleTasks(task.getTask());
				} else {
					List<Task> doneTasks = new ArrayList<Task>();
					for ( Task t : task.getDependencies() ) {
						synchronized (t) {
							RuntimeTaskWrapper<T> at = (RuntimeTaskWrapper<T>)t;
							if ( at.getTaskState() != WrapperTaskState.FINISHED ) {
								at.addDependent(task);
							} else {
								doneTasks.add(at);
							}
						}
					}
					task.removeDependency(doneTasks);
					if ( task.getDependencies() != aeminium.runtime.Runtime.NO_DEPS ){
						task.setTaskState(WrapperTaskState.WAITING_FOR_DEPENDENCIES);
						waitingForDeps.add(task);
					} else {
						running.add(task);
						task.setTaskState(WrapperTaskState.RUNNING);
						prioritizer.scheduleTasks(task.getTask());
					}
				}
			}
		}
	}
	
	// task finished to run 
	public void taskFinished(T task) {
		RuntimeTaskWrapper<T> wtask = (RuntimeTaskWrapper<T>)task.getData(TASK_DATA_KEY);
		synchronized (this) {
			synchronized (wtask) {
				log.info("task finished ==> " + wtask);
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
	protected void taskCompleted(RuntimeTaskWrapper<T> task) {
		synchronized (this) {
			synchronized (task) {
				if ( task.getTaskState() == WrapperTaskState.WAITING_FOR_CHILDREN ) {
					waitingForChildren.remove(task);
				}
				task.setTaskState(WrapperTaskState.FINISHED);
				// callback 
				task.taskCompleted();
				if ( task.getParent() != aeminium.runtime.Runtime.NO_PARENT ) {
					RuntimeTaskWrapper<T> parent = (RuntimeTaskWrapper<T>)task.getParent();
					synchronized (parent) {
						parent.deleteChildTask(task);
						if ( !parent.hasChildren() && parent.getTaskState() == WrapperTaskState.WAITING_FOR_CHILDREN) {
							taskCompleted(parent);
						}
					}
				}
				List<T> readyTasks = new ArrayList<T>();
				for ( Task t : task.getDependents() ) {
					synchronized (t) {
						@SuppressWarnings("unchecked")
						RuntimeTaskWrapper<T> at = (RuntimeTaskWrapper<T>)t;
						at.removeDependency(task);
						if ( at.getDependencies() == aeminium.runtime.Runtime.NO_DEPS ) {
							waitingForDeps.remove(at);
							running.add(at);
							at.setTaskState(WrapperTaskState.RUNNING);
							readyTasks.add(at.getTask());
						}
					}
				}
				prioritizer.scheduleTasks((T[]) readyTasks.toArray(rta));
				log.info("schedule tasks ==> " + readyTasks);
				
				//TODO: we should drop mapping but then we would get into trouble
				//      of another task gets scheduled which depends on this one. If 
				//      it is not in the mapping we don't know it is already finished
				//      or has not yet been scheduled.
				
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