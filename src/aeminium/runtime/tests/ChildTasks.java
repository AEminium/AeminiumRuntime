package aeminium.runtime.tests;

import org.junit.Test;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class ChildTasks extends BaseTest {
	@Test
	public void childTasks() {
		Runtime rt = getRuntime();
		rt.init();
		
		Task t1 = createTask(rt, 2);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
			
		rt.shutdown();
	}
	
	public Task createTask(final Runtime rt, final int level ) {
		return rt.createNonBlockingTask(new Body() {
			
			@Override
			public void execute(Task current) {
				if ( level > 0 ) {
					rt.schedule(createTask(rt, level-1), current, Runtime.NO_DEPS);
				}
			}
		}, Runtime.NO_HINTS);
	}
}
