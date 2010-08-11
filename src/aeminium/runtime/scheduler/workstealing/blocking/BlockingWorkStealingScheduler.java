package aeminium.runtime.scheduler.workstealing.blocking;

import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.scheduler.workstealing.WorkStealingScheduler;
import aeminium.runtime.scheduler.workstealing.WorkerThread;
import aeminium.runtime.task.RuntimeTask;

public final class BlockingWorkStealingScheduler<T extends RuntimeTask> extends AbstractScheduler<T> implements WorkStealingScheduler<T>{
	protected ConcurrentLinkedQueue<WorkerThread<T>> parkedThreads;
	protected WorkerThread<T>[] threads;
	protected Deque<T>[] taskQueues;
	protected RuntimeEventManager eventManager = null;
	protected AtomicInteger counter;
	protected final int maxQueueLength;
	protected static final boolean pollFirst = Configuration.getProperty(BlockingWorkStealingScheduler.class, "pollFirst", false);
	
	public BlockingWorkStealingScheduler() {
		super();
		maxQueueLength = Configuration.getProperty(getClass(), "maxQueueLength", 3);
	}

	public BlockingWorkStealingScheduler(int maxParallelism) {
		super(maxParallelism);
		maxQueueLength = Configuration.getProperty(getClass(), "maxQueueLength", 3);
	}
	
	@Override
	public final void registerThread(WorkerThread<T> thread) {
	}
	
	@Override
	public final void unregisterThread(WorkerThread<T> thread) {
		counter.decrementAndGet();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(RuntimeEventManager eventManager) {
		this.eventManager = eventManager;
		parkedThreads = new ConcurrentLinkedQueue<WorkerThread<T>>();
		threads =  new WorkerThread[getMaxParallelism()];
		taskQueues = new Deque[threads.length];
		counter = new AtomicInteger(threads.length);
				
		// initialize data structures
		for ( int i = 0; i < threads.length; i++ ) {
			threads[i] = new WorkerThread<T>(i, this);
			taskQueues[i] = threads[i].getTaskList();
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
		threads = null;
		taskQueues = null;
		parkedThreads = null;
		counter = null;
	}

	@Override
	public final void scheduleTask(T task) {
		WorkerThread<T> thread = getNextThread();
		Deque<T> taskQueue = taskQueues[thread.getIndex()];
		if ( taskQueue.size() < maxQueueLength ) {
			addTask(taskQueue, task);
			signalWork(thread);
		} else {
			try {
				task.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public final void scheduleTasks(Collection<T> tasks) {
		WorkerThread<T> thread = getNextThread();
		Deque<T> taskQueue = taskQueues[thread.getIndex()];
		for ( T task : tasks ) {
			addTask(taskQueue, task);
		}
		signalWork(thread);
	}

	protected final void addTask(Deque<T> q, T task) {
		while ( !q.offerFirst(task) ) {
			// loop until we could add it 
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
	
	@Override
	public final void parkThread(WorkerThread<T> thread) {
		eventManager.signalThreadSuspend(thread);
		parkedThreads.add(thread);
		LockSupport.park(thread);
	}
	
	@Override
	public final T scanQueues() {
		for ( Deque<T> q : taskQueues ) {
			T task;
			if ( pollFirst ) {
				task = q.pollFirst();
			} else {
				task = q.pollLast();
			}
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
