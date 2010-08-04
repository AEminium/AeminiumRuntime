package aeminium.runtime.scheduler.workstealing.polling;

import java.util.Collection;
import java.util.Deque;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.scheduler.workstealing.WorkStealingScheduler;
import aeminium.runtime.scheduler.workstealing.WorkerThread;
import aeminium.runtime.task.RuntimeTask;

public class PollingWorkStealingScheduler<T extends RuntimeTask> extends AbstractScheduler<T> implements WorkStealingScheduler<T> {
	protected ConcurrentLinkedQueue<WorkerThread<T>> parkedThreads = new ConcurrentLinkedQueue<WorkerThread<T>>();
	protected ThreadLocal<WorkerThread<T>> currentThread = new ThreadLocal<WorkerThread<T>>();
	protected WorkerThread<T>[] threads;
	protected Deque<T>[] taskQueues;
	
	public PollingWorkStealingScheduler(EnumSet<Flags> flags) {
		super(flags);
	}

	public void registerThread(WorkerThread<T> thread) {
		synchronized (this) {
			currentThread.set(thread);
			this.notify();
		}
		parkThread(thread);
	}
	
	@Override
	public void init() {
		threads = new WorkerThread[getMaxParallelism()];
		taskQueues = new Deque[threads.length];
				
		// initialize data structures
		for ( int i = 0; i < threads.length; i++ ) {
			threads[i] = new WorkerThread<T>(i, this);
			taskQueues[i] = threads[i].getTaskList();
		}
		
		// start and register threads threads
		for ( WorkerThread<T> thread : threads ) {
			thread.start();
			try {
				synchronized (this) {
					this.wait();					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
	
	@Override
	public void shutdown() {
		for ( WorkerThread<T> thread : threads ){
			thread.shutdown();
		}

		for ( WorkerThread<T> thread : parkedThreads ) {
			LockSupport.unpark(thread);
		}
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
