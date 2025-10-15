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

package aeminium.runtime.implementations.implicitworkstealing.scheduler;

public interface WorkStealingQueue<E> {
	/**
	 * Pushed the element onto the queue. If the queue is full
	 * the method will try to grow the queue. If the maximum queue
	 * size is reached the method will throw an Error;
	 *
	 * @param e
	 */
	public void push(E e);
	/**
	 * Pops the next element off the queue. If the queue is empty
	 * the method returns null;
	 *
	 * @param e
	 */
	public E pop();
	/**
	 * Returns the next element in the queue without removing it. If
	 * the queue is empty the method returns null;
	 *
	 * @param e
	 */
	public E peek();
	/**
	 * Tries to steal a task from the of the queue. If successful the
	 * method returns the stolen task otherwise null;
	 *
	 * @return
	 */
	public E tryStealing();
	/**
	 * Tries to peek the task that could be stolen. The returned task is
	 * not removed from the queue.
	 *
	 * @return
	 */
	public E peekSteeling();
	/**
	 * Checks whether the queue is empty.
	 *
	 * @return
	 */
	public boolean isEmpty();
	/**
	 * Checks whether the queue is full.
	 *
	 * @return
	 */
	public boolean isFull();
	/**
	 * Gives an approximation of the queue length. NOTE: Because
	 * of the concurrent updates the returned value is more supposed
	 * be an educated guess rather than an exact value.
	 *
	 * @return
	 */
	public int size();
}
