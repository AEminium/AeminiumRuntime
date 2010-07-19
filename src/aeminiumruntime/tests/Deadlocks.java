package aeminiumruntime.tests;

import java.util.Arrays;

import org.junit.Test;
import org.junit.Assert;

import aeminiumruntime.Body;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;

public class Deadlocks extends NoExitTest {

	@Test
	public void testRoundDeadlock() {

		Runtime rt = getRuntime();
		rt.init();
		rt.startDebug();

		Task t1 = rt.createNonBlockingTask(createBody(1), Runtime.NO_HINTS);
		Task t2 = rt.createNonBlockingTask(createBody(2), Runtime.NO_HINTS);
		Task t3 = rt.createNonBlockingTask(createBody(3), Runtime.NO_HINTS);
		Task t4 = rt.createNonBlockingTask(createBody(4), Runtime.NO_HINTS);
		try {
			rt.schedule(t1, Runtime.NO_PARENT, Arrays.asList(t4));
			rt.schedule(t2, Runtime.NO_PARENT, Arrays.asList(t1));
			rt.schedule(t3, Runtime.NO_PARENT, Arrays.asList(t2));
			rt.schedule(t4, Runtime.NO_PARENT, Arrays.asList(t3));
			Assert.fail();
			rt.shutdown();
		} catch (ExitException e) {
			Assert.assertEquals("Exit status when scheduling a deadlock.", 1, e.status);
		}
	}

	@Test
	public void testSelfDeadlock() {
		Runtime rt = getRuntime();
		rt.init();
		rt.startDebug();

		Task t1 = rt.createNonBlockingTask(createBody(1), Runtime.NO_HINTS);
		try {
			rt.schedule(t1, Runtime.NO_PARENT, Arrays.asList(t1));
			Assert.fail();
			rt.shutdown();
		} catch (ExitException e) {
			Assert.assertEquals("Exit status when scheduling a deadlock.", 1, e.status);
		}
		
	}

	public Body createBody(final int i) {
		return new Body() {
			public void execute(Task parent) {
				System.out.println("Task " + i);
			}
		};
	}

}