package aeminium.runtime.implementations.implicitworkstealing.scheduler.stealing;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

/*
 * Task Stealing Algorithm that steals from those who have stolen before.
 */
public class Revenge implements WorkStealingAlgorithm {
	private ConcurrentLinkedQueue<WorkStealingThread> parkedThreads;
	private WorkStealingThread[] threads;
	private WorkStealingThread[] thiefes;
	private Queue<ImplicitTask> submissionQueue;

	@Override
	public final void init(WorkStealingThread[] threads, Queue<ImplicitTask> submissionQueue) {
		this.threads         = threads;
		this.thiefes         = new WorkStealingThread[threads.length];
		this.parkedThreads   = new ConcurrentLinkedQueue<WorkStealingThread>();
		this.submissionQueue = submissionQueue;
	}

	@Override
	public final void shutdown() {
		this.threads         = null;
		this.parkedThreads   = null;
		this.submissionQueue = null;
	}

	@Override
	public final WorkStealingThread signalWorkInLocalQueue(WorkStealingThread current) {
		WorkStealingThread thread = threads[(current.index+1)%threads.length];
		parkedThreads.remove(thread);
		return thread;
	}

	@Override
	public final WorkStealingThread signalWorkInSubmissionQueue() {
		WorkStealingThread thread = parkedThreads.poll();
		return thread;
	}

	@Override
	public final ImplicitTask stealWork(WorkStealingThread current) {
		if ( submissionQueue != null && !submissionQueue.isEmpty() ) {
			ImplicitTask task = submissionQueue.poll();
			if ( task != null ) {
				return task;
			}
		}

		if ( thiefes[current.index] != null ) {
			if ( thiefes[current.index].getLocalQueueSize() > 0  ) {
				ImplicitTask task =  thiefes[current.index].getTaskQueue().tryStealing();
				if ( task != null ) {
					return task;
				}
			}
		}


		WorkStealingThread biggest = threads[(current.index+threads.length-1)%threads.length];
		int smallestCount     = 0; //smallest.getLocalQueueSize();
		for ( int i = 1;  i < threads.length ; i++ ) {
			WorkStealingThread next = threads[(current.index+threads.length-i)%threads.length];
			ImplicitTask task = next.peekStealingTask();
			if ( task != null  && task.level < smallestCount  ) {
				smallestCount = task.level;
				biggest      = next;
			}
		}

		thiefes[biggest.index] = current;

		return biggest.tryStealingTask();
	}

	@Override
	public final void threadGoingToPark(WorkStealingThread thread) {
		parkedThreads.add(thread);
	}
}
