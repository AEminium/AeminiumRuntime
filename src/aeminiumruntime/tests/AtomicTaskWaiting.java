package aeminiumruntime.tests;

import org.junit.Test;

import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;

public class AtomicTaskWaiting extends BaseTest {
	@Test(timeout=5000)
	public void runAtomicTaskWaitingTest() {
		Runtime rt = getRuntime();
		rt.init();
		
		DataGroup dg = rt.createDataGroup();
		rt.schedule(createAtomicTask(rt, dg, 110), Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(createAtomicTask(rt, dg, 120), Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(createAtomicTask(rt, dg, 130), Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(createAtomicTask(rt, dg, 140), Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(createAtomicTask(rt, dg, 150), Runtime.NO_PARENT, Runtime.NO_DEPS);
		
		rt.shutdown();
	}
	
	public Task createAtomicTask(final Runtime rt, DataGroup group, final int delay) {
		return rt.createAtomicTask(new Body() {
			@Override
			public void execute(Task current) {
				// let's create some sub tasks
				rt.schedule(rt.createNonBlockingTask(new Body() {
					@Override
					public void execute(Task current) {
						System.out.println("Sub Task waiting for "+ (delay+1) + " ms");
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
					@Override
					public String toString() {
						return ""+(delay+1);
					}
				}, Runtime.NO_HINTS), current, Runtime.NO_DEPS);
				rt.schedule(rt.createNonBlockingTask(new Body() {
					@Override
					public void execute(Task current) {
						System.out.println("Sub Task waiting for "+ (delay+2) + " ms");
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
					@Override
					public String toString() {
						return ""+(delay+2);
					}

				}, Runtime.NO_HINTS), current, Runtime.NO_DEPS);
	
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
		} , group, Runtime.NO_HINTS);
	}
}
