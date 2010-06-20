package aeminiumruntime.tools.benchmark;

import aeminiumruntime.Body;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;
import aeminiumruntime.simpleparallel.ParallelRuntime;

public class IndependentTaskGraph implements IBenchmark {
	private final String name = "IndepenetTaskGraph";
	private final int[] COUNTS = {100, 1000, 10000};
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run(IReporter reporter) {
		for (int COUNT : COUNTS) {
			runTest(reporter, COUNT);
		}
	}

	private void runTest(IReporter reporter, int count) {
		Runtime rt = new ParallelRuntime();
		rt.init();
		
		long start = System.nanoTime();
		for(int i = 0; i < count; i++ ) {
			Task nextTask = createTask(rt);
			rt.schedule(nextTask, Runtime.NO_DEPS);
		}
		long end = System.nanoTime();
		String result = String.format("Run %10d tasks in %12d ns ==> %10d ns per task execution.", count, (end-start), ((end-start)/count));
		reporter.reportLn(result);
		rt.shutdown();
	}
	
	private Task createTask(Runtime rt) {
		return rt.createNonBlockingTask(new Body() {
			
			@Override
			public void execute(Task parent) {
				// DO NOTHING
			}
		});
	}
}