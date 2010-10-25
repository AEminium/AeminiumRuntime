package aeminium.runtime.implementations.implicitworkstealing.scheduler.stealing;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkerThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public class StealFromMaxQueue implements WorkStealingAlgorithm {
	private ConcurrentLinkedQueue<WorkerThread> parkedThreads;
	private WorkerThread[] threads;
	private Queue<ImplicitTask> submissionQueue;
	
	@Override
	public final void init(WorkerThread[] threads, Queue<ImplicitTask> submissionQueue) {
		this.threads         = threads;
		this.parkedThreads   = new ConcurrentLinkedQueue<WorkerThread>();
		this.submissionQueue = submissionQueue;
	}

	@Override
	public final void shutdown() {
		this.threads         = null;
		this.parkedThreads   = null;
		this.submissionQueue = null;
	}

	@Override
	public final WorkerThread singalWorkInLocalQueue(WorkerThread current) {
		WorkerThread thread = threads[(current.index+1)%threads.length];
		parkedThreads.remove(thread);		
		return thread;
	}

	@Override
	public final WorkerThread singalWorkInSubmissionQueue() {
		WorkerThread thread = parkedThreads.poll();
		return thread;
	}

	@Override
	public final ImplicitTask stealWork(WorkerThread current) {
		if ( submissionQueue != null && !submissionQueue.isEmpty() ) {
			ImplicitTask task = submissionQueue.poll();
			if ( task != null ) {
				return task;
			}
		}
		
		WorkerThread richest = threads[(current.index+threads.length-1)%threads.length];
		int richestCount     = richest.getLocalQueueSize();
		for ( int i = 1;  i < threads.length ; i++ ) {
			WorkerThread next = threads[(current.index+threads.length-i)%threads.length];
			int nextCount = next.getLocalQueueSize();
			if ( richestCount < nextCount ) {
				richestCount = nextCount;
				richest      = next;
			}
		}
		
		return richest.tryStealingTask();
	}

	@Override
	public final void threadGoingToPark(WorkerThread thread) {
		parkedThreads.add(thread);		
	}
}
