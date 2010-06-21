package aeminiumruntime.queue;

import aeminiumruntime.Task;


public class QGraph {
	private final QTaskList waitingForDeps     = new QTaskList("waitingForDeps");
	private final QTaskList running            = new QTaskList("running");
	private final QTaskList waitingForChildren = new QTaskList("waitingForChildren");
	private final QScheduler scheduler;
	
	public QGraph(QScheduler scheduler ) {
		this.scheduler = scheduler;
	}
	
	public boolean addTask(QAbstractTask task) {
		synchronized (this) {
			synchronized (task) {
				task.setGraph(this);
				if ( task.getDependencies() == aeminiumruntime.Runtime.NO_DEPS ) {
					running.addListItem(task);
					task.setTaskState(QTaskState.RUNNING);
					scheduler.schedule(task);
				} else {
					for ( Task t : task.getDependencies() ) {
						synchronized (t) {
							QAbstractTask at = (QAbstractTask)t;
							if ( at.getTaskState() != QTaskState.FINISHED ) {
								at.addDependent(task);
							}
						}
					}
					if ( task.getDependencies() != aeminiumruntime.Runtime.NO_DEPS ){
						task.setTaskState(QTaskState.WAITING_FOR_DEPENDENCIES);
						waitingForDeps.addListItem(task);
					} else {
						running.addListItem(task);
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
				running.removeListItem(task);
				if (task.hasChildren()) {
					waitingForChildren.addListItem(task);
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
					waitingForChildren.removeListItem(task);
				}
				task.setTaskState(QTaskState.FINISHED);
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
							// schedule task
							waitingForDeps.removeListItem(at);
							running.addListItem(at);
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
