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

package aeminium.runtime;

public interface ErrorHandler {
	/**
	 * Called when the execution of task's body threw exception 'e'.
	 *
	 * @param task
	 * @param exception
	 */
	public void handleTaskException(final Task task, final Throwable t);

	/**
	 * Called when a deadlock in locking has been detected.
	 */
	public void handleLockingDeadlock();

	/**
	 * Called when a cycle in task's dependencies is detected.
	 *
	 * @param task
	 */
	public void handleDependencyCycle(final Task task);

	/**
	 * Called if task has been scheduled twice.
	 *
	 * @param task
	 */
	public void handleTaskDuplicatedSchedule(final Task task);

	/**
	 * Called when some internal error occurred.
	 *
	 * @param err
	 */
	public void handleInternalError(final Error err);
}
