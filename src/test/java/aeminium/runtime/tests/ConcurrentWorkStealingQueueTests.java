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

package aeminium.runtime.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import aeminium.runtime.implementations.implicitworkstealing.scheduler.ConcurrentWorkStealingQueue;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingQueue;


public class ConcurrentWorkStealingQueueTests {
	@Test
	public void testGrowQueue() {
		WorkStealingQueue<String> wsq = new ConcurrentWorkStealingQueue<String>(2);
		String control = "Hello World!";
		for( char c: control.toCharArray() ) {
			wsq.push(""+c);
		}

		StringBuilder sb = new StringBuilder();
		while ( !wsq.isEmpty() ) {
			sb.append(wsq.tryStealing());
		}
		assertTrue( control.equals(sb.toString()));
	}

	@Test
	public void testOrdering() {
		WorkStealingQueue<String> wsq = new ConcurrentWorkStealingQueue<String>(2);
		String control = "Hello World!";
		for( char c: control.toCharArray() ) {
			wsq.push(""+c);
		}

		String hello = wsq.tryStealing() +
		               wsq.tryStealing() +
		               wsq.tryStealing() +
		               wsq.tryStealing() +
		               wsq.tryStealing();

		StringBuilder sb = new StringBuilder();
		while ( !wsq.isEmpty() ) {
			sb.append(wsq.pop());
		}
		assertEquals(control, hello+sb.reverse());
	}

	@Test
	public void checkSize() {
		WorkStealingQueue<String> wsq = new ConcurrentWorkStealingQueue<String>(2);
		assertTrue( wsq.size() == 0 );
		wsq.push("1");
		wsq.push("2");
		assertEquals( 2, wsq.size());
		wsq.tryStealing();
		wsq.tryStealing();
		assertEquals( 0, wsq.size());
		wsq.push("3");
		wsq.push("4");
		wsq.push("5");
		assertEquals( 3, wsq.size());
		wsq.push("6");
		wsq.push("7");
		assertEquals( 5, wsq.size());
	}

	@Test(timeout=3000)
	public void stessTest() {
		final int N = 10000000;
		final WorkStealingQueue<Integer> wsq = new ConcurrentWorkStealingQueue<Integer>(13);

		Thread producer = new Thread(new Runnable() {
			@Override
			public void run() {
				int counter = 0;
				for (int i = 0; i < N ; i++ ) {
					wsq.push(i);
					if ( i % 12 == 0 ) {
						Integer value = wsq.pop();
						if (value != null ) {
							counter++;
						}
					}
				}
				wsq.push(Integer.MAX_VALUE);
				wsq.push(Integer.MAX_VALUE);
				System.out.println("Producer consumed " + counter);
			}
		});

		Thread consumer1 = new Thread(new Runnable() {
			@Override
			public void run() {
				int counter = 0;
				while (true) {
					Integer value = wsq.tryStealing();
					if ( value != null ) {
						if ( value == Integer.MAX_VALUE ) {
							break;
						}
						counter++;
					}
				}
				System.out.println("Consumer1 consumed " + counter);
			}
		});

		Thread consumer2 = new Thread(new Runnable() {
			@Override
			public void run() {
				int counter = 0;
				while ( true ) {
					Integer value = wsq.tryStealing();
					if ( value != null ) {
						if ( value == Integer.MAX_VALUE ) {
							break;
						}
						counter++;
					}
				}
				System.out.println("Consumer2 consumed " + counter);
			}
		});


		consumer1.start();
		consumer2.start();
		producer.start();

		try {
			consumer1.join();
			consumer2.join();
			producer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
