package aeminium.runtime.scheduler.workstealing;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.task.implicit.ImplicitTask;

public final class WorkerThread<T extends ImplicitTask> extends Thread {
	public final int index;
	protected volatile boolean shutdown = false;
	protected final WorkStealingScheduler<T> scheduler;
	protected final int pollingCount;
	protected WorkStealingQueue<T> taskQueue;
	protected static final AtomicInteger IdGenerator = new AtomicInteger(0);
	
	public WorkerThread(int index, WorkStealingScheduler<T> scheduler) {
		this.index = index;
		this.scheduler = scheduler;
		setName("WorkerThread-"+IdGenerator.incrementAndGet());
		pollingCount = Configuration.getProperty(getClass(), "pollingCount", 5);
	}
	
	public final WorkStealingQueue<T> getTaskQueue() {
		return taskQueue;
	}
	
	public final void shutdown() {
		shutdown = true;
	}
	
	@Override
	public final void run() {
		taskQueue = new ConcurrentWorkStealingQueue<T>(13);
		int pollCounter = pollingCount;
		scheduler.registerThread(this);
		while (!shutdown) {
			T task = null;
			task = taskQueue.pop();
			if ( task != null ) {
				try {
					task.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// scan for other queues
				task = scheduler.scanQueues(this);
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
						Thread.yield();
					}
				}
			}
		}
		scheduler.unregisterThread(this);
		taskQueue = null;
	}

	public final T scan() {
		WorkStealingQueue<T> queue = taskQueue;
		if ( queue != null ) {
			return queue.tryStealing();
		}
		return null;
	}
	
	public final String toString() {
		return "WorkerThread<" + index + ">";
	}
}
