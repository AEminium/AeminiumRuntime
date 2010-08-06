package aeminium.runtime.scheduler.workstealing.polling;

import java.util.Collection;
import java.util.Deque;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.scheduler.workstealing.WorkStealingScheduler;
import aeminium.runtime.scheduler.workstealing.WorkerThread;
import aeminium.runtime.task.RuntimeTask;

public class PollingWorkStealingScheduler<T extends RuntimeTask> extends AbstractScheduler<T> implements WorkStealingScheduler<T> {
	protected ConcurrentLinkedQueue<WorkerThread<T>> parkedThreads;
	protected ThreadLocal<WorkerThread<T>> currentThread;
	protected WorkerThread<T>[] threads;
	protected Deque<T>[] taskQueues;
	protected AtomicInteger counter;
	
	public PollingWorkStealingScheduler(EnumSet<Flags> flags) {
		super(flags);
	}
	
	public PollingWorkStealingScheduler(int maxParallelism, EnumSet<Flags> flags) {
		super(maxParallelism, flags);
	}

	public void registerThread(WorkerThread<T> thread) {
		currentThread.set(thread);
	}
	
	public void unregisterThread(WorkerThread<T> thread) {
		if ( 0 ==  counter.decrementAndGet() ) {
			synchronized (this) {
				this.notifyAll();
			}
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init() {
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
		for ( WorkerThread<T> thread : threads ){
			thread.shutdown();
			LockSupport.unpark(thread);
		}

		synchronized (this) {
			while (counter.get() > 0 ) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		threads = null;
		taskQueues = null;
		currentThread = null;
		parkedThreads = null;
		counter = null;
	}

	@Override
	public void scheduleTask(T task) {
		Deque<T> taskQueue = taskQueues[currentThread().getIndex()];
		addTask(taskQueue, task);
		signalWork();
	}

	@Override
	public void scheduleTasks(Collection<T> tasks) {
		Deque<T> taskQueue = taskQueues[currentThread().getIndex()];
		for ( T task : tasks ) {
			addTask(taskQueue, task);
		}
		signalWork();
	}

	protected void addTask(Deque<T> q, T task) {
		task.setScheduler(this);
		while ( !q.offerFirst(task) ) {
			// loop until we could add it 
		}
	}
	
	protected WorkerThread<T> currentThread() {
		WorkerThread<T> current = currentThread.get();
		if ( current == null ) {
			current = parkedThreads.poll();
			if ( current == null ) {
				current = threads[0];
			}
		}
		return current;
	}
	
	public void signalWork() {
		WorkerThread<T> thread = parkedThreads.poll();
		if ( thread != null ) {
			LockSupport.unpark(thread);
		}
	}
	
	public void parkThread(WorkerThread<T> thread) {
		parkedThreads.add(thread);
		LockSupport.parkNanos(thread, 100000);
	}

	@Override
	public T scanQueues() {
		for ( Deque<T> q : taskQueues ) {
			T task = q.pollLast();
			if ( task != null ) {
				return task;
			}
		}
		return null;
	}

	@Override
	public void taskFinished(T task) {
		// disable running count of abstract super class
	}

	@Override
	public void taskPaused(T task) {
		// disable paused count of abstract super class
	}

	@Override
	public void taskResume(T task) {
		scheduleTask(task);
	}
}
