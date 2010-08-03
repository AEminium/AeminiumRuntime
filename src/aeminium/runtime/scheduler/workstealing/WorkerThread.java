package aeminium.runtime.scheduler.workstealing;

import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.task.RuntimeTask;

public class WorkerThread<T extends RuntimeTask> extends Thread {
	protected final List<T> taskQueue;
	protected final int index;
	protected boolean shutdown = false;
	protected final WorkStealingScheduler<T> scheduler;
	
	public WorkerThread(int index, WorkStealingScheduler<T> scheduler) {
		this.taskQueue = new LinkedList<T>();
		this.index = index;
		this.scheduler = scheduler;
	}

	public int getIndex() {
		return index;
	}
	
	public List<T> getTaskList() {
		return taskQueue;
	}
	
	public void shutdown() {
		shutdown = true;
	}
	
	@Override
	public void run() {
		scheduler.registerThread(this);
		while (!shutdown) {
			T task = null;
			synchronized (taskQueue) {
				if ( !taskQueue.isEmpty() ) {
					task = taskQueue.remove(0);
				}
			}
			if ( task != null ) {
				//System.out.println(""+this +" works on " + task);
				executeTask(task);
			} else {
				// scan for other queues
				task = scheduler.scanQueues();
				if ( task != null ) {
					executeTask(task);
				} else {
					//System.out.println("" + this + "is parking");
					scheduler.parkThread(this);
				}
			}
		}
	}
	
	protected void executeTask(T task) {
		try {
			task.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return "WorkerThread<" + index + ">";
	}
}
