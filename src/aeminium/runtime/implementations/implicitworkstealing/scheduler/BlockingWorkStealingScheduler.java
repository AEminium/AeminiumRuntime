package aeminium.runtime.implementations.implicitworkstealing.scheduler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.events.EventManager;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public final class BlockingWorkStealingScheduler {
	protected final ImplicitWorkStealingRuntime rt;
	protected ConcurrentLinkedQueue<WorkerThread> parkedThreads;
	protected WorkerThread[] threads;
	protected EventManager eventManager = null;
	protected AtomicInteger counter;
	protected Queue<ImplicitTask> submissionQueue;
	protected final int maxParallelism;
	protected static final boolean oneTaskPerLevel = Configuration.getProperty(BlockingWorkStealingScheduler.class, "oneTaskPerLevel", true);
	protected static final int maxQueueLength = Configuration.getProperty(BlockingWorkStealingScheduler.class, "maxQueueLength", 0);
	
	public BlockingWorkStealingScheduler(ImplicitWorkStealingRuntime rt) {
		this.rt        = rt;
		maxParallelism = Configuration.getProcessorCount();
	}

	public BlockingWorkStealingScheduler(ImplicitWorkStealingRuntime rt, int maxParallelism) {
		this.rt             = rt;	
		this.maxParallelism = maxParallelism;
	}
	
	public void init(EventManager eventManager) {
		this.eventManager    = eventManager;
		this.parkedThreads   = new ConcurrentLinkedQueue<WorkerThread>();
		this.threads         = new WorkerThread[maxParallelism];
		this.counter         = new AtomicInteger(threads.length);
		this.submissionQueue = new ConcurrentLinkedQueue<ImplicitTask>();
				
		// initialize data structures
		for ( int i = 0; i < threads.length; i++ ) {
			threads[i] = new WorkerThread(rt, i);
		}
		
		// start and register threads threads
		for ( WorkerThread thread : threads ) {
			thread.start();
		}
	}

	public void shutdown() {
		counter.set(threads.length);
		while ( counter.get() > 0 ) {
			for ( WorkerThread thread : threads ){
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

	public final void registerThread(WorkerThread thread) {
		eventManager.signalNewThread(thread);
	}

	public final void unregisterThread(WorkerThread thread) {
		counter.decrementAndGet();
	}

	public final void scheduleTask(ImplicitTask task) {
		if ( 0 < maxQueueLength) {
			Thread thread = Thread.currentThread();
			if ( thread instanceof WorkerThread) {
				WorkerThread wthread = (WorkerThread)thread;
				WorkStealingQueue<ImplicitTask> taskQueue = wthread.getTaskQueue();
				if ( taskQueue.size() < maxQueueLength ) {
					taskQueue.push(task);
					signalWork();
				} else {
					task.invoke(rt);
				}
			} else {
				submissionQueue.add(task);
				signalWork();
			}
		} else {
			Thread thread = Thread.currentThread();
			if ( thread instanceof WorkerThread ) {
				// worker thread 
				WorkerThread wthread = (WorkerThread)thread;
				if ( oneTaskPerLevel ) {
					WorkStealingQueue<ImplicitTask> taskQueue = wthread.getTaskQueue();
					ImplicitTask head = taskQueue.peek();
					if ( head != null && head.level == task.level ) {
						task.invoke(rt);
					} else {
						taskQueue.push(task);
						signalWork(wthread);
					}
				} else {
					wthread.getTaskQueue().push(task);
					signalWork(wthread);
				}
			} else {
				// external thread
				submissionQueue.add(task);
				signalWork();
			}
		}
	}

	protected final WorkerThread getNextThread() {
		Thread thread = Thread.currentThread(); 
		if ( thread instanceof WorkerThread ) {
			return (WorkerThread) thread;
		} else {
			thread = parkedThreads.poll();
			if ( thread == null ) {
				thread = threads[0];
			}
		}
		return (WorkerThread) thread;
	}

	public final void signalWork(WorkerThread thread) {
		// TODO: need to fix that to wake up thread waiting for objects to complete
		LockSupport.unpark(thread);
		WorkerThread threadParked = parkedThreads.poll();
		if ( threadParked != null ) {
			LockSupport.unpark(threadParked);
		}
	}
	
	public final void signalWork() {
		WorkerThread threadParked = parkedThreads.poll();
		if ( threadParked != null ) {
			LockSupport.unpark(threadParked);
		}
	}
	
	public final void parkThread(WorkerThread thread) {
		eventManager.signalThreadSuspend(thread);
		parkedThreads.add(thread);
		LockSupport.park(thread);
	}
	
	public final ImplicitTask scanQueues(WorkerThread thread) {
		if ( submissionQueue != null && !submissionQueue.isEmpty() ) {
			ImplicitTask task = submissionQueue.poll();
			if ( task != null ) {
				return task;
			}
		}
		for ( WorkerThread t : threads ) {
			ImplicitTask task = t.scan();
			if ( task != null ) {
				return task;
			}
		}
		return null;
	}
}
