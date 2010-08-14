package aeminium.runtime.scheduler.workstealing.polling;

import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.scheduler.workstealing.WorkStealingQueue;
import aeminium.runtime.scheduler.workstealing.WorkStealingScheduler;
import aeminium.runtime.scheduler.workstealing.WorkerThread;
import aeminium.runtime.task.implicit.ImplicitTask;

public final class PollingWorkStealingScheduler<T extends ImplicitTask> extends AbstractScheduler<T> implements WorkStealingScheduler<T> {
	protected ConcurrentLinkedQueue<WorkerThread<T>> parkedThreads;
	protected WorkerThread<T>[] threads;
	protected RuntimeEventManager eventManager = null;
	protected AtomicInteger counter;
	protected Queue<T> submissionQueue;
	protected final int maxQueueLength       = Configuration.getProperty(PollingWorkStealingScheduler.class, "maxQueueLength", 3);
	protected final int pollingTimeout       = Configuration.getProperty(PollingWorkStealingScheduler.class, "pollingTimeout", 100000);;
	protected static final boolean pollFirst = Configuration.getProperty(PollingWorkStealingScheduler.class, "pollFirst", false);
	
	public PollingWorkStealingScheduler() {
		super();
	}
	
	public PollingWorkStealingScheduler(int maxParallelism) {
		super(maxParallelism);
	}

	public final void registerThread(WorkerThread<T> thread) {
	}
	
	public final void unregisterThread(WorkerThread<T> thread) {
		counter.decrementAndGet();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(RuntimeEventManager eventManager) {
		this.eventManager = eventManager;
		parkedThreads = new ConcurrentLinkedQueue<WorkerThread<T>>();
		threads = new WorkerThread[getMaxParallelism()];
		counter = new AtomicInteger(threads.length);
		submissionQueue = new ConcurrentLinkedQueue<T>();
		
		// initialize data structures
		for ( int i = 0; i < threads.length; i++ ) {
			threads[i] = new WorkerThread<T>(i, this);
		}
		
		// start and register threads threads
		for ( WorkerThread<T> thread : threads ) {
			thread.start();
		}
	}

	@Override
	public void shutdown() {
		while( counter.get() > 0 ) {
			for ( WorkerThread<T> thread : threads ){
				thread.shutdown();
				LockSupport.unpark(thread);
			}
		}

		// cleanup
		threads         = null;
		parkedThreads   = null;
		counter         = null;
		submissionQueue = null;
	}

	@Override
	public final void scheduleTask(T task) {
		Thread thread = Thread.currentThread();
		if ( thread instanceof WorkerThread<?>) {
			@SuppressWarnings("unchecked")
			WorkerThread<T> wthread = (WorkerThread<T>)thread;
			WorkStealingQueue<T> taskQueue = wthread.getTaskQueue();
			if ( taskQueue.size() < maxQueueLength ) {
				taskQueue.push(task);
				signalWork();
			} else {
				try {
					task.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			submissionQueue.add(task);
			signalWork();
		}
	}


	
	public final void signalWork() {
		WorkerThread<T> thread = parkedThreads.poll();
		if ( thread != null ) {
			LockSupport.unpark(thread);
		}
	}
	
	
	public final void parkThread(WorkerThread<T> thread) {
		eventManager.signalThreadSuspend(thread);
		parkedThreads.add(thread);
		LockSupport.parkNanos(thread, pollingTimeout);
	}
	

	@Override
	public final T scanQueues(WorkerThread<T> thread) {
		if ( submissionQueue != null && !submissionQueue.isEmpty() ) {
			T task = submissionQueue.poll();
			if ( task != null ) {
				return task;
			}
		}
		for ( int i = 0; i < threads.length; i++ ) {
			// round robin
			T task = threads[(i+thread.index+1)%threads.length].scan();
			if ( task != null ) {
				return task;
			}
		}
		return null;
	}

	@Override
	public final void taskFinished(T task) {
		// disable running count of abstract super class
	}

	@Override
	public final void taskPaused(T task) {
		// disable paused count of abstract super class
	}

	@Override
	public final void taskResume(T task) {
		scheduleTask(task);
	}
}
