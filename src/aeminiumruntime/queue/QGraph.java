package aeminiumruntime.queue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import aeminiumruntime.Task;


public class QGraph {
	private final List<QAbstractTask>  waitingForDeps= new LinkedList<QAbstractTask>();
	private final List<QAbstractTask>  running = new LinkedList<QAbstractTask>();
	private final List<QAbstractTask>  waitingForChildren = new LinkedList<QAbstractTask>();

	private final QScheduler scheduler;
	
	public QGraph(QScheduler scheduler ) {
		this.scheduler = scheduler;
	}
	
	public boolean addTask(QAbstractTask task) {
		synchronized (this) {
			synchronized (task) {
				task.setGraph(this);
				if ( task.getDependencies() == aeminiumruntime.Runtime.NO_DEPS ) {
					running.add(task);
					task.setTaskState(QTaskState.RUNNING);
					scheduler.schedule(task);
				} else {
					List<Task> doneTasks = new ArrayList<Task>();
					for ( Task t : task.getDependencies() ) {
						synchronized (t) {
							QAbstractTask at = (QAbstractTask)t;
							if ( at.getTaskState() != QTaskState.FINISHED ) {
								at.addDependent(task);
							} else {
								//task.removeDependency(at);
								doneTasks.add(at);
							}
						}
					}
					task.removeDependency(doneTasks);
					if ( task.getDependencies() != aeminiumruntime.Runtime.NO_DEPS ){
						task.setTaskState(QTaskState.WAITING_FOR_DEPENDENCIES);
						waitingForDeps.add(task);
					} else {
						running.add(task);
						task.setTaskState(QTaskState.RUNNING);
						scheduler.schedule(task);
					}
				}
			}
		}
		return true;
	}
	
	// task finished to run 
	public void taskFinshed(QAbstractTask task) {
		assert ( task.getGraph() == this );
		synchronized (this) {
			synchronized (task) {
				running.remove(task);
				// callback 
				task.taskFinished();
				if (task.hasChildren()) {
					waitingForChildren.add(task);
					task.setTaskState(QTaskState.WAITING_FOR_CHILDREN);
				} else {
					taskCompleted(task);
				}
			}
		}
	}

	// have to synchronize on task and this
	public void taskCompleted(QAbstractTask task) {
		synchronized (this) {
			synchronized (task) {
				if ( task.getTaskState() == QTaskState.WAITING_FOR_CHILDREN ) {
					waitingForChildren.remove(task);
				}
				task.setTaskState(QTaskState.FINISHED);
				// callback 
				task.taskCompleted();
				if ( task.getParent() != aeminiumruntime.Runtime.NO_PARENT ) {
					QAbstractTask parent = (QAbstractTask)task.getParent();
					synchronized (parent) {
						parent.deleteChildTask(task);
						if ( !parent.hasChildren() && parent.getTaskState() == QTaskState.WAITING_FOR_CHILDREN) {
							taskCompleted(parent);
						}
					}
				}
				for ( Task t : task.getDependents() ) {
					synchronized (t) {
						QAbstractTask at = (QAbstractTask)t;
						at.removeDependency(task);
						if ( at.getDependencies() == aeminiumruntime.Runtime.NO_DEPS ) {
							waitingForDeps.remove(at);
							running.add(at);
							at.setTaskState(QTaskState.RUNNING);
							scheduler.schedule(at);
						}
					}
				}

				if (waitingForChildren.isEmpty() && waitingForDeps.isEmpty() && running.isEmpty()) {
					this.notifyAll();
				}
			}
		}
	}
	
	public void waitUntilEmpty() {
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
}
