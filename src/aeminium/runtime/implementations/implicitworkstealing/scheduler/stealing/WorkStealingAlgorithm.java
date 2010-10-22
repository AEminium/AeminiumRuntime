package aeminium.runtime.implementations.implicitworkstealing.scheduler.stealing;

import java.util.Queue;

import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkerThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public interface WorkStealingAlgorithm {
	/** 
	 * Initialize the work stealing algorithm with the array of available
	 * worker threads.
	 * 
	 * @param threads Array of available worker threads threads.
	 */
	public void init(WorkerThread[] threads, Queue<ImplicitTask> submissionQueue);
	
	/**
	 * Shutsdown the work stealing algorithm.
	 */
	public void shutdown();
	
	/**
	 * Is called when a new task has been inserted into the submission queue.
	 * The method needs to return the task that should be unparked to process
	 * the new item. If no thread should be unpacked (e.g., all threads are 
	 * running). 
	 * 
	 * @return The thread that should be woken up or null.
	 */
	public WorkerThread singalWorkInSubmissionQueue();
	
	/**
	 * This method is called when a new item has been added into the local 
	 * queue of a worker thread. The method should determine which thread 
	 * should be unparked/notified about this event.
	 * 
	 * 
	 * @param current The thread which enqueued the new item into its
	 *                local queue.
	 * @return The thread that should be woken up.
	 */
	public WorkerThread singalWorkInLocalQueue(WorkerThread current);

	/**
	 * This method is called before a worker thread is parked/pasued.
	 * 
	 * @param current The thread that is going to be parked.
	 */
	public void threadGoingToPark(WorkerThread current);

	/**
	 * This method is called when a thread runs out of local work. 
	 * The method is responsible to find another task the current thread
	 * can work on and return it. If no work can be found the method returns null.
	 * 
	 * @param current The current worker thread that looks for work.
	 * @return Either task with work or null
	 */
	public ImplicitTask stealWork(WorkerThread current);
}
