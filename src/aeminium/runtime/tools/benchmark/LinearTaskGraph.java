package aeminium.runtime.tools.benchmark;

import java.util.Arrays;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class LinearTaskGraph implements Benchmark {
	private static final String name = "LinearTaskGraph";
	private final int[] COUNTS = {100, 1000, 10000, 100000};
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run(Reporter reporter) {
		for (int COUNT : COUNTS) {
			runTest(reporter, COUNT);
			reporter.flush();
		}
	}

	private void runTest(Reporter reporter, int count) {
		Runtime rt = Factory.getRuntime();
		rt.init();
		
		long start = System.nanoTime();
		Task previousTask = createTask(rt, "Task-0");
		rt.schedule(previousTask, Runtime.NO_PARENT, Runtime.NO_DEPS);
		
		for(int i = 1; i < count; i++ ) {
			Task nextTask = createTask(rt, "Task-"+i);
			rt.schedule(nextTask, Runtime.NO_PARENT, Arrays.asList(previousTask));
			previousTask = nextTask;
		}
		rt.shutdown();
		long end = System.nanoTime();
		
		String result = String.format("Run %10d tasks in %12d ns ==> %10d ns per task | %6d tasks/second.", count, (end-start), ((end-start)/count),  (1000000000/((end-start)/count)));
		reporter.reportLn(result);

	}
	
	private Task createTask(Runtime rt, final String name) {
		return rt.createNonBlockingTask(new Body() {
			
			@Override
			public void execute(Runtime rt, Task parent) {
				// DO NOTHING
			}
			
			@Override
			public String toString() {
				return name;
			}

		}, Runtime.NO_HINTS);
	}
}
