package aeminium.runtime.tests;

import static org.junit.Assert.fail;

import org.junit.Test;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.Task;

public class DoubleScheduleTask extends BaseTest {
	@Test()
	public void scheduleTaskTwice() {
		Runtime rt = getRuntime();
		rt.init();
		
		try { 
			Task t = rt.createNonBlockingTask(new Body() {
				@Override
				public void execute(Runtime rt, Task current) {	
				}
			}, Runtime.NO_HINTS);

			rt.schedule(t, Runtime.NO_PARENT, Runtime.NO_DEPS);
			rt.schedule(t, Runtime.NO_PARENT, Runtime.NO_DEPS);
			fail();
		} catch (RuntimeError e) {
		}
		
		rt.shutdown();
	}
}
