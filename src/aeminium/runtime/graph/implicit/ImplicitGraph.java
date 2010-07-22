package aeminium.runtime.graph.implicit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.CyclicDependencyError;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.graph.AbstractGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.TaskDescription;
import aeminium.runtime.task.implicit.ImplicitTask;
import aeminium.runtime.task.implicit.ImplicitTaskState;

public class ImplicitGraph<T extends ImplicitTask> extends AbstractGraph<T> {
	private final List<T>  waitingForDeps= new LinkedList<T>();
	private final List<T>  running = new LinkedList<T>();
	private final List<T>  waitingForChildren = new LinkedList<T>();
	private final boolean checkForCycles;
	private final ImplicitTask[] ita = new ImplicitTask[0];
	
	public ImplicitGraph(EnumSet<Flags> flags, RuntimePrioritizer<T> prioritizer) {
		super(flags, prioritizer);
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
	
	@Override
	public void addTask(T task, Task parent, Collection<T> deps) {
		synchronized (this) {
 			synchronized (task) {
 				// setup dependendecies
 				if ( deps != Runtime.NO_DEPS ) {
 					task.setDependencies(new ArrayList<Task>(deps));
 				} else {
 					task.setDependencies(Runtime.NO_DEPS);
 				}
 				task.setParent(parent);
 				if ( parent != Runtime.NO_PARENT ) {
 					((T)parent).addChildTask(task);
 					task.setParent(parent);
 				}
 				
 				if ( checkForCycles ) {
 					Collection<Task> taskDeps = Collections.unmodifiableList((List<? extends Task>) task.getDependencies());
 					checkForCycles(task, taskDeps);
 				}
 				
 				
 				if ( task.getDependencies() == aeminium.runtime.Runtime.NO_DEPS ) {
					running.add(task);
					task.setTaskState(ImplicitTaskState.RUNNING);
					prioritizer.scheduleTasks(task);
				} else {
					List<Task> doneTasks = new ArrayList<Task>();
					for ( Task t : task.getDependencies() ) {
						synchronized (t) {
							T at = (T)t;
							if ( at.getTaskState() != ImplicitTaskState.FINISHED ) {
								at.addDependent(task);
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
						prioritizer.scheduleTasks(task);
					}
				}
			}
		}
	}
	
	protected void checkForCycles(T task, Collection<Task> deps) {
		if ( deps == Runtime.NO_DEPS ) {
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
			Collection<Task> nextDeps;
			synchronized (dep) {
				 nextDeps = Collections.unmodifiableList((List<? extends Task>) dep.getDependencies());
			}
			checkForCycles(task, nextDeps);
		}
	}
	
	// task finished to run 
	public void taskFinished(T task) {
		synchronized (this) {
			synchronized (task) {
				running.remove(task);
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
	protected void taskCompleted(ImplicitTask task) {
		synchronized (this) {
			synchronized (task) {
				if ( task.getTaskState() == ImplicitTaskState.WAITING_FOR_CHILDREN ) {
					waitingForChildren.remove(task);
				}
				task.setTaskState(ImplicitTaskState.FINISHED);
				// callback 
				task.taskCompleted();
				if ( task.getParent() != aeminium.runtime.Runtime.NO_PARENT ) {
					@SuppressWarnings("unchecked")
					T parent = (T)task.getParent();
					synchronized (parent) {
						parent.deleteChildTask(task);
						if ( !parent.hasChildren() && parent.getTaskState() == ImplicitTaskState.WAITING_FOR_CHILDREN) {
							taskCompleted(parent);
						}
					}
				}
				ArrayList<T> readyTasks = new ArrayList<T>(10);
				for ( Task t : task.getDependents() ) {
					synchronized (t) {
						@SuppressWarnings("unchecked")
						T at = (T)t;
						at.removeDependency(task);
						if ( at.getDependencies() == Runtime.NO_DEPS ) {
							waitingForDeps.remove(at);
							running.add(at);
							at.setTaskState(ImplicitTaskState.RUNNING);
							//prioritizer.scheduleTasks(at);
							readyTasks.add(at);
						}
					}
				}

				// trigger prioritize in case he was caching some tasks
				prioritizer.scheduleTasks((T[]) readyTasks.toArray(ita));
				
				// wake up waiting threads 
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
		return TaskDescription.create(task, task.getDependencies().size(), task.getDependents().size());
	}
}