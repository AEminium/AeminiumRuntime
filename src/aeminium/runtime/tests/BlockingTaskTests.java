package aeminium.runtime.tests;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class BlockingTaskTests extends BaseTest {

	@Test
	public void singleTask() {
		Runtime rt = getRuntime();
		rt.init();

		Task task = rt.createBlockingTask(new Body() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				getLogger().info("Single Task");	
			}
		}, Runtime.NO_HINTS);
		rt.schedule(task, Runtime.NO_PARENT, Runtime.NO_DEPS);

		rt.shutdown();		
	}


	@Test
	public void manyTasks() {
		Runtime rt = getRuntime();
		rt.init();

		final int TASK_COUNT = 200;
		final AtomicInteger counter = new AtomicInteger();
		
		for( int i = 0 ; i < TASK_COUNT ; i++ ) {
			Task task = rt.createBlockingTask(new Body() {
				@Override
				public void execute(Runtime rt, Task current) throws Exception {
					counter.incrementAndGet();
				}
			}, Runtime.NO_HINTS);
			rt.schedule(task, Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		rt.shutdown();
		assertTrue( counter.get() == TASK_COUNT );
	}

	@Test
	public void manyTasksMix() {
		Runtime rt = getRuntime();
		rt.init();
		
		final int TASK_COUNT = 200;
		final AtomicInteger counter = new AtomicInteger();
		
		for( int i = 0 ; i < TASK_COUNT ; i++ ) {
			Task taskB = rt.createBlockingTask(new Body() {
				@Override
				public void execute(Runtime rt, Task current) throws Exception {
					counter.incrementAndGet();
				}
			}, Runtime.NO_HINTS);
			rt.schedule(taskB, Runtime.NO_PARENT, Runtime.NO_DEPS);

			Task taskN = rt.createNonBlockingTask(new Body() {
				@Override
				public void execute(Runtime rt, Task current) throws Exception {
					counter.incrementAndGet();
				}
			}, Runtime.NO_HINTS);
			rt.schedule(taskN, Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		rt.shutdown();
		assertTrue( counter.get() == 2*TASK_COUNT );
	}
}
