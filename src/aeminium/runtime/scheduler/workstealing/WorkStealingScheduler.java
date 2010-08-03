package aeminium.runtime.scheduler.workstealing;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;

public class WorkStealingScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {
	protected ConcurrentLinkedQueue<WorkerThread<T>> parkedThreads = new ConcurrentLinkedQueue<WorkerThread<T>>();
	protected ThreadLocal<WorkerThread<T>> currentThread = new ThreadLocal<WorkerThread<T>>();
	protected WorkerThread<T>[] threads;
	protected List<T>[] taskQueues;
	
	public WorkStealingScheduler(EnumSet<Flags> flags) {
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
		threads = new WorkerThread[Runtime.getRuntime().availableProcessors()];
		taskQueues = new List[threads.length];
				
		// initialize data strucutres
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
		//System.out.println("schedule task " + task);
		task.setScheduler(this);
		List<T> taskQueue = taskQueues[currentThread().getIndex()];
		synchronized (taskQueue) {
			taskQueue.add(0, task);
		}
		signalWork();
	}

	@Override
	public void scheduleTasks(Collection<T> tasks) {
		//System.out.println("schedule task " + tasks);
		List<T> taskQueue = taskQueues[currentThread().getIndex()];
		synchronized (taskQueue) {
			taskQueue.addAll(0, tasks);
		}
		signalWork();
	}

	protected WorkerThread<T> currentThread() {
		WorkerThread<T> current = currentThread.get();
		if ( current == null ) {
			try {
				current = parkedThreads.remove();
			} catch ( NoSuchElementException e) {
			} finally {
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
			if ( !parkedThreads.isEmpty() ) {
				WorkerThread<T> thread = parkedThreads.poll();
				if ( thread != null ) {
					LockSupport.unpark(thread);
				}
			}
	}
	
	public void parkThread(WorkerThread<T> thread) {
		parkedThreads.add(thread);
		LockSupport.parkNanos(thread, 100000);
		
	}

	public T scanQueues() {
		List<T> threadQueue = taskQueues[currentThread().getIndex()];
		for ( List<T> q : taskQueues ) {
			synchronized (q) {
				if ( !q.isEmpty() ) {
					return q.remove(q.size()-1);
				}
			}
		}
		return null;
	}
}
