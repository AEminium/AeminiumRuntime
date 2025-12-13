/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 *
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

package aeminium.runtime.implementations.implicitworkstealing.scheduler.stealing;

import java.util.Queue;

import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public interface WorkStealingAlgorithm {
	/**
	 * Initialize the work stealing algorithm with the array of available
	 * worker threads.
	 *
	 * @param threads Array of available worker threads threads.
	 */
	public void init(WorkStealingThread[] threads, Queue<ImplicitTask> submissionQueue);

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
	 * This method is part of the CRITICAL PATH.
	 *
	 * @return The thread that should be woken up or null.
	 */
	public WorkStealingThread signalWorkInSubmissionQueue();

	/**
	 * This method is called when a new item has been added into the local
	 * queue of a worker thread. The method should determine which thread
	 * should be unparked/notified about this event.
	 *
	 * This method is part of the CRITICAL PATH.
	 *
	 *
	 * @param current The thread which enqueued the new item into its
	 *                local queue.
	 * @return The thread that should be woken up.
	 */
	public WorkStealingThread signalWorkInLocalQueue(WorkStealingThread current);

	/**
	 * This method is called before a worker thread is parked/pasued.
	 *
	 * @param current The thread that is going to be parked.
	 */
	public void threadGoingToPark(WorkStealingThread current);

	/**
	 * This method is called when a thread runs out of local work.
	 * The method is responsible to find another task the current thread
	 * can work on and return it. If no work can be found the method returns null.
	 *
	 * @param current The current worker thread that looks for work.
	 * @return Either task with work or null
	 */
	public ImplicitTask stealWork(WorkStealingThread current);
}
