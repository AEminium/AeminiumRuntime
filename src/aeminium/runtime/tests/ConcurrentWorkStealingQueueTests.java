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
}
