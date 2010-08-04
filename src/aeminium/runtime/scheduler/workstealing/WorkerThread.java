package aeminium.runtime.scheduler.workstealing;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import aeminium.runtime.task.RuntimeTask;

public class WorkerThread<T extends RuntimeTask> extends Thread {
	protected final Deque<T> taskQueue;
	protected final int index;
	protected boolean shutdown = false;
	protected final WorkStealingScheduler<T> scheduler;
	
	public WorkerThread(int index, WorkStealingScheduler<T> scheduler) {
		this.taskQueue = new LinkedBlockingDeque<T>();
		this.index = index;
		this.scheduler = scheduler;
	}

	public int getIndex() {
		return index;
	}
	
	public Deque<T> getTaskList() {
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
			task = taskQueue.pollFirst();
			if ( task != null ) {
				executeTask(task);
			} else {
				// scan for other queues
				task = scheduler.scanQueues();
				if ( task != null ) {
					executeTask(task);
				} else {
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
