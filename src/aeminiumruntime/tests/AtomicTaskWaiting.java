package aeminiumruntime.tests;

import org.junit.Test;

import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;

public class AtomicTaskWaiting extends BaseTest {
	@Test
	public void runAtomicTaskWaitingTest() {
		Runtime rt = getRuntime();
		rt.init();
		
		DataGroup dg = rt.createDataGroup();
		rt.schedule(createAtomicTask(rt, dg, 101), Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(createAtomicTask(rt, dg, 101), Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(createAtomicTask(rt, dg, 103), Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(createAtomicTask(rt, dg, 104), Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(createAtomicTask(rt, dg, 105), Runtime.NO_PARENT, Runtime.NO_DEPS);
		
		rt.shutdown();
	}
	
	public Task createAtomicTask(Runtime rt, DataGroup group, final int delay) {
		return rt.createAtomicTask(new Body() {
			@Override
			public void execute(Task current) {
				System.out.println("Task waiting for "+delay + " ms");
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
		} , group);
	}
}
