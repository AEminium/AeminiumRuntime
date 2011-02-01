package aeminium.runtime.tests;

import org.junit.Test;

import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class NestedAtomicTaskWaiting extends BaseTest {
	
	@Test
	public void runAtomicTaskWaitingTest() {
		Runtime rt = getRuntime();
		rt.init();
		
		DataGroup dg = rt.createDataGroup();
		Task t1 = createAtomicTask(rt, dg, "TASK-1", 3);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t2 = createAtomicTask(rt, dg, "TASK-2", 5);
		rt.schedule(t2, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t3 = createAtomicTask(rt, dg, "TASK-3", 2);
		rt.schedule(t3, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t4 = createAtomicTask(rt, dg, "TASK-4", 4);
		rt.schedule(t4, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t5 = createAtomicTask(rt, dg, "TASK-5", 2);
		rt.schedule(t5, Runtime.NO_PARENT, Runtime.NO_DEPS);
		
		rt.shutdown();
	}
	
	public Task createAtomicTask(final Runtime rt, final DataGroup group, final String prefix, final int level) {
		final int delay = 20*(level+1);
		return rt.createAtomicTask(new Body() {
			@Override
			public void execute(Runtime rt, Task current) {
				if ( 0 < level ) {
					// let's create some sub tasks
					Task t1 = createAtomicTask(rt, group, prefix+".1", level-1);
					rt.schedule(t1, current, Runtime.NO_DEPS);

					Task t2 = createAtomicTask(rt, group, prefix+".2", level-1);
					rt.schedule(t2, current, Runtime.NO_DEPS);
				}
				getLogger().info(prefix + " waiting for " + delay + " ms");
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
			@Override
			public String toString() {
				return ""+delay;
			}
		} , group, Runtime.NO_HINTS);
	}
}