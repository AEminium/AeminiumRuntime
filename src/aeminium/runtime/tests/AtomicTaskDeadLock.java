package aeminium.runtime.tests;

import org.junit.Test;



import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class AtomicTaskDeadLock  extends BaseTest {
	@Test(expected=Exception.class, timeout=2000)
	public void SUPPOSED_TO_FAIL___createAtomicTaskDeadLock() {
		Runtime rt = getRuntime();
		rt.init();

		DataGroup dg1 = rt.createDataGroup();
		DataGroup dg2 = rt.createDataGroup();
		
		rt.schedule(createAtomicTask(rt, dg1, dg2), Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(createAtomicTask(rt, dg2, dg1), Runtime.NO_PARENT, Runtime.NO_DEPS);
		
		rt.shutdown();
	}
	
	private Task createAtomicTask(final Runtime rt, final DataGroup dg1, final DataGroup dg2) {
		return rt.createAtomicTask(new Body() {
			
			@Override
			public void execute(Task current) {
				getLogger().info("Atomic Task for data group : " + dg1);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rt.schedule(createAtomicTask(rt, dg2), current, Runtime.NO_DEPS);
			}
		}, dg1, Runtime.NO_HINTS);
	}

	private Task createAtomicTask(final Runtime rt, final DataGroup dg) {
		return rt.createAtomicTask(new Body() {
			
			@Override
			public void execute(Task current) {
				getLogger().info("Atomic Sub-Task for data group : " + dg);				
			}
		},  dg, Runtime.NO_HINTS);
	}
}
