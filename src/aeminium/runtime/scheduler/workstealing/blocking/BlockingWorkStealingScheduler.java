package aeminium.runtime.scheduler.workstealing.blocking;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.implementations.AbstractRuntime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.scheduler.workstealing.WorkStealingQueue;
import aeminium.runtime.scheduler.workstealing.WorkStealingScheduler;
import aeminium.runtime.scheduler.workstealing.WorkerThread;
import aeminium.runtime.task.implicit.ImplicitTask;

public final class BlockingWorkStealingScheduler<T extends ImplicitTask> extends AbstractScheduler<T> implements WorkStealingScheduler<T>{
	protected ConcurrentLinkedQueue<WorkerThread<T>> parkedThreads;
	protected WorkerThread<T>[] threads;
	protected RuntimeEventManager eventManager = null;
	protected AtomicInteger counter;
	protected Queue<T> submissionQueue;
	protected static final boolean oneTaskPerLevel = Configuration.getProperty(BlockingWorkStealingScheduler.class, "oneTaskPerLevel", true);
	
	public BlockingWorkStealingScheduler() {
		super();
	}

	public BlockingWorkStealingScheduler(int maxParallelism) {
		super(maxParallelism);
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(RuntimeEventManager eventManager) {
		super.init();
		AbstractRuntime.scheduler = null;
		this.eventManager = eventManager;
		parkedThreads = new ConcurrentLinkedQueue<WorkerThread<T>>();
		threads =  new WorkerThread[getMaxParallelism()];
		counter = new AtomicInteger(threads.length);
		submissionQueue = new ConcurrentLinkedQueue<T>();
				
		// initialize data structures
		for ( int i = 0; i < threads.length; i++ ) {
			threads[i] = new WorkerThread<T>(i, this, eventManager);
		}
		
		// start and register threads threads
		for ( WorkerThread<T> thread : threads ) {
			thread.start();
		}
	}

	@Override
	public void shutdown() {
		counter.set(threads.length);
		while ( counter.get() > 0 ) {
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
	public final void registerThread(WorkerThread<T> thread) {
	}
	
	@Override
	public final void unregisterThread(WorkerThread<T> thread) {
		counter.decrementAndGet();
	}


	@Override
	public final void scheduleTask(T task) {
		Thread thread = Thread.currentThread();
		if ( thread instanceof WorkerThread<?> ) {
			// worker thread 
			@SuppressWarnings("unchecked")
			WorkerThread<T> wthread = (WorkerThread<T>)thread;
			if ( oneTaskPerLevel ) {
				WorkStealingQueue<T> taskQueue = wthread.getTaskQueue();
				T head = taskQueue.peek();
				if ( head != null && head.level == task.level ) {
					try {
						task.call();
					} catch (Exception e) {
						e.printStackTrace();
					}
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

	@SuppressWarnings("unchecked")
	protected final WorkerThread<T> getNextThread() {
		Thread thread = Thread.currentThread(); 
		if ( thread instanceof WorkerThread<?> ) {
			return (WorkerThread<T>) thread;
		} else {
			thread = parkedThreads.poll();
			if ( thread == null ) {
				thread = threads[0];
			}
		}
		return (WorkerThread<T>) thread;
	}

	public final void signalWork(WorkerThread<T> thread) {
		LockSupport.unpark(thread);
		WorkerThread<T> threadParked = parkedThreads.poll();
		if ( threadParked != null ) {
			LockSupport.unpark(threadParked);
		}
	}
	

	public final void signalWork() {
		WorkerThread<T> threadParked = parkedThreads.poll();
		if ( threadParked != null ) {
			LockSupport.unpark(threadParked);
		}
	}
	
	@Override
	public final void parkThread(WorkerThread<T> thread) {
		eventManager.signalThreadSuspend(thread);
		parkedThreads.add(thread);
		LockSupport.park(thread);
	}
	
	@Override
	public final T scanQueues(WorkerThread<T> thread) {
		if ( submissionQueue != null && !submissionQueue.isEmpty() ) {
			T task = submissionQueue.poll();
			if ( task != null ) {
				return task;
			}
		}
		for ( WorkerThread<T> t : threads ) {
			T task = t.scan();
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
