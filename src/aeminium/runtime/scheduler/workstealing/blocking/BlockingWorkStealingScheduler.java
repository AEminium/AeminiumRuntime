package aeminium.runtime.scheduler.workstealing.blocking;

import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.scheduler.workstealing.WorkStealingScheduler;
import aeminium.runtime.scheduler.workstealing.WorkerThread;
import aeminium.runtime.task.implicit.ImplicitTask;

public final class BlockingWorkStealingScheduler<T extends ImplicitTask> extends AbstractScheduler<T> implements WorkStealingScheduler<T>{
	protected Deque<WorkerThread<T>> parkedThreads;
	protected WorkerThread<T>[] threads;
	protected Deque<T>[] taskQueues;
	protected RuntimeEventManager eventManager;
	protected AtomicInteger counter;
	protected final int maxQueueLength       = Configuration.getProperty(BlockingWorkStealingScheduler.class, "maxQueueLength", 3);
	protected static final boolean pollFirst = Configuration.getProperty(BlockingWorkStealingScheduler.class, "pollFirst", false);
	protected Deque<T> globalQueue;
	
	public BlockingWorkStealingScheduler() {
		super();
	}

	public BlockingWorkStealingScheduler(int maxParallelism) {
		super(maxParallelism);
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
		threads           = new WorkerThread[getMaxParallelism()];
		taskQueues        = new Deque[threads.length];
		counter           = new AtomicInteger(threads.length);
		globalQueue       = new LinkedBlockingDeque<T>();
		parkedThreads     = new LinkedBlockingDeque<WorkerThread<T>>();
				
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
		threads     = null;
		taskQueues  = null;
		counter     = null;
		globalQueue = null;
	}

	@Override
	public final void scheduleTask(T task) {
		Thread thread = Thread.currentThread();
		if ( ! (thread instanceof WorkerThread<?> ) ) {
			globalQueue.add(task);
			LockSupport.unpark(threads[0]);
		} else {
			@SuppressWarnings("unchecked")
			WorkerThread<T> wthread = (WorkerThread<T>)thread;
			Deque<T> q = wthread.getTaskList();
			if ( wthread.queueLevel < task.level && q.size() < maxQueueLength ) {
				q.addFirst(task);
				wthread.queueLevel = task.level;
				signalWork(wthread);
			} else {
				try {
					task.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public final void scheduleTasks(Collection<T> tasks) {
		WorkerThread<T> thread = getNextThread();
		Deque<T> taskQueue = taskQueues[thread.index];
		for ( T task : tasks ) {
			while ( !taskQueue.offerFirst(task) ) { /*loop until we could add it*/}
		}
		signalWork(thread);
	}

	@SuppressWarnings("unchecked")
	protected final WorkerThread<T> getNextThread() {
		Thread thread = Thread.currentThread(); 
		if ( thread instanceof WorkerThread<?> ) {
			return (WorkerThread<T>) thread;
		} else {
			thread = threads[0];
		}
		return (WorkerThread<T>) thread;
	}

	public final void signalWork(WorkerThread<T> thread) {
		//LockSupport.unpark(threads[(thread.index+1) % threads.length]);
		LockSupport.unpark(parkedThreads.poll());
	}
	
	@Override
	public final void parkThread(WorkerThread<T> thread) {
		eventManager.signalThreadSuspend(thread);
		parkedThreads.add(thread);
		LockSupport.park(thread);
	}
	
	@Override
	public final T scanQueues(WorkerThread<T> thread) {
		int index = thread.index;
		for(int i = threads.length; i > 0 ; i--) {
			Deque<T> q = taskQueues[(index+i)%taskQueues.length];
			T task;
			if ( pollFirst ) {
				task = q.pollFirst();
			} else {
				task = q.pollLast();
			}
			if ( task != null) {
				return task;
			}
		}
		T task = globalQueue.pollFirst();
		if ( task != null ) {
			return task;
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
