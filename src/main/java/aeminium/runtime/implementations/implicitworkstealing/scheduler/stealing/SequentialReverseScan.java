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
import java.util.concurrent.ConcurrentLinkedQueue;

import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;
/*
 * Task Stealing Algorithm that steals the first task going backwards on the
 * list of threads from the current task.
 */
public final class SequentialReverseScan implements WorkStealingAlgorithm {
	private ConcurrentLinkedQueue<WorkStealingThread> parkedThreads;
	private WorkStealingThread[] threads;
	private Queue<ImplicitTask> submissionQueue;

	@Override
	public final void init(WorkStealingThread[] threads, Queue<ImplicitTask> submissionQueue) {
		this.threads         = threads;
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

		for ( int i = 0;  i < threads.length ; i++ ) {
			WorkStealingThread next = threads[(current.index+threads.length-i)%threads.length];
			ImplicitTask task = next.tryStealingTask();
			if ( task != null ) {
				return task;
			}
		}

		return null;
	}

	@Override
	public final void threadGoingToPark(WorkStealingThread thread) {
		parkedThreads.add(thread);
	}
}
