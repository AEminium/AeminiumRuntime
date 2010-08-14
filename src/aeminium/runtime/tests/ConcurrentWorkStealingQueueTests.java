package aeminium.runtime.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import aeminium.runtime.scheduler.workstealing.ConcurrentWorkStealingQueue;
import aeminium.runtime.scheduler.workstealing.WorkStealingQueue;


public class ConcurrentWorkStealingQueueTests {
	@Test
	public void testGrowQueue() {
		WorkStealingQueue<String> wsq = new ConcurrentWorkStealingQueue<String>(1);
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
		WorkStealingQueue<String> wsq = new ConcurrentWorkStealingQueue<String>(1);
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
