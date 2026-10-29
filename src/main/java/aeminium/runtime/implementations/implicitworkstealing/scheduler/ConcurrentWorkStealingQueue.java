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

import java.util.concurrent.atomic.AtomicReferenceArray;


/**
 * Work stealing queue that has only one thread adding elements to it
 * at the beginning while multiple threads can steal elements from the
 * end. The design was heavily inspired by the work stealing queue
 * implementation of the fork/join framework.
 *
 * @author sven
 *
 * @param <E>
 */
public class ConcurrentWorkStealingQueue<E> implements WorkStealingQueue<E> {
	protected volatile AtomicReferenceArray<E> buffer;
	protected int length;
	protected volatile int base; // base of the circular buffer
	protected int sp;            // next free slot
	public final static int MAX_QUEUE_POWER = 28;

	public ConcurrentWorkStealingQueue(int power) {
		if ( power < 2 || power >= MAX_QUEUE_POWER ) {
			throw new Error("Invalid initial size: 2 <= power < MAX_QUEUE_SIZE.");
		}
		this.base        = 0;
		this.sp          = 0;
		this.length      = 0x1<<power;
		@SuppressWarnings("unchecked")
		E[] bufferArray = (E[])new Object[length];
		this.buffer      = new AtomicReferenceArray<E>(bufferArray);
	}

	public final boolean isEmpty() {
		return base == sp ;
	}

	public final boolean isFull() {
		return (base == ((length+sp+1)%length))  ;
	}

	public final E tryStealing() {
		E value = buffer.get(base) ;
		if ( value != null ) {
			if ( buffer.compareAndSet(base, value, null) ) {
				base = (length+base+1) % length;
			} else {
				return null;
			}
		}
		return value;
	}

	public final E peekSteeling() {
		return buffer.get(base);
	}

	public final void push(E e) {
		if ( isFull() ) {
			growQueue();
		}
		buffer.set(sp, e);
		sp = ((length+sp+1)%length);
	}

	public final E pop() {
		int newSp = (length+sp-1)%length;
		E result = buffer.get(newSp);
		if ( result != null ) {
			if ( buffer.compareAndSet(newSp, result, null) ) {
				sp = newSp;
			} else {
				result = null;
			}
		}
		return result;
}

	public final E peek() {
		return buffer.get((length+sp-1)%length);
	}

	public final int size() {
		if ( base < sp ) {
			return sp - base;
		} else if ( sp < base ) {
			return (length ) - (base - sp);
		} else {
			return 0;
		}
	}

	protected final void growQueue() {
		int newLength = length << 1;
		if ( newLength >= (1<<MAX_QUEUE_POWER) ) {
			throw new Error("Cannot grow queue because maximum size has been reached.");
		}
		@SuppressWarnings("unchecked")
		AtomicReferenceArray<E> newBuffer = new AtomicReferenceArray<E>((E[])new Object[newLength]);

		// need to start with base
		E value = null;
		while ( !isEmpty() && value == null ) {
			value = buffer.get(base);
			if ( value != null ) {
				if ( buffer.compareAndSet(base, value, null)) {
					break;
				} else {
					value = null;
				}
			}
		}

		if ( !isEmpty() && value != null ) {
			newBuffer.set(base, value);
			int newSp = (newLength+base+1)%length;
			int oldBase = (length+base+1)%length;
			while ( oldBase != sp ) {
				value = buffer.get(oldBase);
				newBuffer.set(newSp, value);
				newSp = (newLength+newSp+1)%newLength;
				oldBase = (length+oldBase+1)%length;
			}
			sp     = newSp;
			length = newLength;
			buffer = newBuffer;
		}

	}

	@Override
	public final String toString() {
		return "[base="+base+","+"sp="+sp+",len="+length+"] "+buffer.toString();
	}
}
