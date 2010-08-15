package aeminium.runtime.tests;

import java.util.Arrays;

import org.junit.Test;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class LinearDependencies extends BaseTest {
	@Test
	public void linearDependenciesTest() {
		Runtime rt = getRuntime();
		rt.init();
		
		Task t1 = rt.createNonBlockingTask(new Body() {
			@Override
			public void execute(Runtime rt, Task current) {
				// wait some time to allow other task to be inserted 
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public String toString() {
				return "t1";
			}
		}, Runtime.NO_HINTS);
		
		Task t2 = rt.createNonBlockingTask(new Body() {
			@Override
			public void execute(Runtime rt, Task current) {
			}
			@Override
			public String toString() {
				return "t2";
			}
		}, Runtime.NO_HINTS);
		
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(t2, Runtime.NO_PARENT, Arrays.asList(t1));
		
		rt.shutdown();
	}
}
