package aeminium.runtime.scheduler.workstealing.polling;

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
import aeminium.runtime.task.implicit.ImplicitTask;

public final class PollingWorkStealingScheduler<T extends ImplicitTask> extends AbstractScheduler<T> implements WorkStealingScheduler<T> {
	protected ConcurrentLinkedQueue<WorkerThread<T>> parkedThreads;
	protected ThreadLocal<WorkerThread<T>> currentThread;
	protected WorkerThread<T>[] threads;
	protected Deque<T>[] taskQueues;
	protected RuntimeEventManager eventManager = null;
	protected AtomicInteger counter;
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
		currentThread.set(thread);
	}
	
	public final void unregisterThread(WorkerThread<T> thread) {
		counter.decrementAndGet();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(RuntimeEventManager eventManager) {
		this.eventManager = eventManager;
		parkedThreads = new ConcurrentLinkedQueue<WorkerThread<T>>();
		currentThread = new ThreadLocal<WorkerThread<T>>();
		threads = new WorkerThread[getMaxParallelism()];
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
		while( counter.get() > 0 ) {
			for ( WorkerThread<T> thread : threads ){
				thread.shutdown();
				LockSupport.unpark(thread);
			}
		}

		// cleanup
		threads = null;
		taskQueues = null;
		currentThread = null;
		parkedThreads = null;
		counter = null;
	}

	@Override
	public final void scheduleTask(T task) {
		WorkerThread<T> thread = currentThread();
		Deque<T> taskQueue = thread.getTaskList();//taskQueues[currentThread().getIndex()];
		if ( taskQueue.size() < maxQueueLength ) {
			addTask(taskQueue, task);
			signalWork();
		} else {
			try {
				//task.setScheduler(this);
				task.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public final void scheduleTasks(Collection<T> tasks) {
		Deque<T> taskQueue = taskQueues[currentThread().index];
		for ( T task : tasks ) {
			addTask(taskQueue, task);
		}
		signalWork();
	}

	protected final void addTask(Deque<T> q, T task) {
		//task.setScheduler(this);
		while ( !q.offerFirst(task) ) {
			// loop until we could add it 
		}
	}
	
	protected final WorkerThread<T> currentThread() {
		WorkerThread<T> current = currentThread.get();
		if ( current == null ) {
			current = parkedThreads.poll();
			if ( current == null ) {
				current = threads[0];
			}
		}
		return current;
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
