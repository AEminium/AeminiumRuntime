package aeminium.runtime.implementations.implicitworkstealing.scheduler;

import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public final class WorkerThread extends Thread {
	protected final ImplicitWorkStealingRuntime rt;
	public final int index;
	public volatile int taskCount;
	protected volatile boolean shutdown = false;
	protected final int pollingCount = Configuration.getProperty(getClass(), "pollingCount", 5);
	protected WorkStealingQueue<ImplicitTask> taskQueue;
	protected static final AtomicInteger IdGenerator = new AtomicInteger(0);
	
	public WorkerThread(ImplicitWorkStealingRuntime rt, int index) {
		this.rt           = rt;
		this.index        = index;
		setName("WorkerThread-"+IdGenerator.incrementAndGet());
	}
	
	public final WorkStealingQueue<ImplicitTask> getTaskQueue() {
		return taskQueue;
	}
	
	public final void shutdown() {
		shutdown = true;
	}
	
	@Override
	public final void run() {
		super.run();
		taskQueue = new ConcurrentWorkStealingQueue<ImplicitTask>(13);
		int pollCounter = pollingCount;
		rt.scheduler.registerThread(this);
		while (!shutdown) {
			ImplicitTask task = null;
			task = taskQueue.pop();
			if ( task != null ) {
				task.invoke(rt);
			} else {
				// scan for other queues
				task = rt.scheduler.scanQueues(this);
				if ( task != null ) {
					task.invoke(rt);
				} else {
					if ( pollCounter == 0) {
						rt.scheduler.parkThread(this);
						pollCounter = pollingCount;
					} else {
						pollCounter--;
						Thread.yield();
					}
				}
			}
		}
		rt.scheduler.unregisterThread(this);
		taskQueue = null;
	}

	public final ImplicitTask scan() {
		WorkStealingQueue<ImplicitTask> queue = taskQueue;
		if ( queue != null ) {
			return queue.tryStealing();
		}
		return null;
	}
	
	public final void progressToCompletion(ImplicitTask toComplete) {
		int pollCounter = pollingCount;
		while ( !toComplete.isCompleted() ) {
			ImplicitTask task = null;
			task = taskQueue.pop();
			if ( task != null ) {
				task.invoke(rt);
			} else {
				// scan for other queues
				task = rt.scheduler.scanQueues(this);
				if ( task != null ) {
					task.invoke(rt);
				} else {
					if ( pollCounter == 0) {
						// reset counter
						pollCounter = pollingCount;
					} else {
						pollCounter--;
						Thread.yield();
					}
				}
			}
		}
	}
	
	public final String toString() {
		return getName();
	}
}
