package aeminium.runtime.tests;

import java.util.Arrays;

import org.junit.Test;

import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class AtomicTaskWaiting extends BaseTest {
	
	@Test
	public void runAtomicTaskWaitingTest() {
		Runtime rt = getRuntime();
		rt.init();
		
		DataGroup dg = rt.createDataGroup();
		Task t1 = createAtomicTask(rt, dg, 110);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t2 = createAtomicTask(rt, dg, 120);
		rt.schedule(t2, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t3 = createAtomicTask(rt, dg, 130);
		rt.schedule(t3, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t4 = createAtomicTask(rt, dg, 140);
		rt.schedule(t4, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t5 = createAtomicTask(rt, dg, 150);
		rt.schedule(t5, Runtime.NO_PARENT, Runtime.NO_DEPS);
		
		rt.shutdown();
	}
	
	public Task createAtomicTask(final Runtime rt, DataGroup group, final int delay) {
		return rt.createAtomicTask(new Body() {
			@Override
			public void execute(Task current) {
				// let's create some sub tasks
				Task t1 = rt.createNonBlockingTask(new Body() {
					@Override
					public void execute(Task current) {
						getLogger().info("Sub Task waiting for "+ (delay+1) + " ms");
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
				}, Runtime.NO_HINTS);
				rt.schedule(t1, current, Runtime.NO_DEPS);
				
				Task t2 = rt.createNonBlockingTask(new Body() {
					@Override
					public void execute(Task current) {
						getLogger().info("Sub Task waiting for "+ (delay+2) + " ms");
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

				}, Runtime.NO_HINTS);
				rt.schedule(t2, current, Arrays.asList(t1));
	
				getLogger().info("Task waiting for "+delay + " ms");
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
