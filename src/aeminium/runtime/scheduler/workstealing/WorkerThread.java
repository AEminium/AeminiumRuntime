package aeminium.runtime.scheduler.workstealing;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.task.RuntimeTask;

public final class WorkerThread<T extends RuntimeTask> extends Thread {
	protected final Deque<T> taskQueue;
	protected final int index;
	protected volatile boolean shutdown = false;
	protected final WorkStealingScheduler<T> scheduler;
	protected final int pollingCount;
	protected static final AtomicInteger IdGenerator = new AtomicInteger(0);
	
	public WorkerThread(int index, WorkStealingScheduler<T> scheduler) {
		this.taskQueue =  new LinkedBlockingDeque<T>();
		this.index = index;
		this.scheduler = scheduler;
		setName("WorkerThread-"+IdGenerator.incrementAndGet());
		pollingCount = Configuration.getProperty(getClass(), "pollingCount", 5);
	}

	public final int getIndex() {
		return index;
	}
	
	public final Deque<T> getTaskList() {
		return taskQueue;
	}
	
	public final void shutdown() {
		shutdown = true;
	}
	
	@Override
	public final void run() {

		int pollCounter = pollingCount;
		scheduler.registerThread(this);
		while (!shutdown) {
			T task = null;
			task = taskQueue.pollFirst();
			if ( task != null ) {
				try {
					task.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// scan for other queues
				task = scheduler.scanQueues();
				if ( task != null ) {
					try {
						task.call();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if ( pollCounter == 0) {
						scheduler.parkThread(this);
						pollCounter = pollingCount;
					} else {
						pollCounter--;
					}
				}
			}
		}
		scheduler.unregisterThread(this);
	}

	public final String toString() {
		return "WorkerThread<" + index + ">";
	}
}
