package aeminiumruntime.tools.benchmark;

import aeminiumruntime.Body;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;
import aeminiumruntime.simpleparallel.ParallelRuntime;

public class TaskCreationBenchmark implements IBenchmark {
	private final String name = "TaskCreation";
	private final int[] COUNTS = { 100, 1000, 10000, 100000, 1000000};
	
	@Override
	public void run(IReporter reporter) {
		Body body = new Body() {
			@Override
			public void execute(Task parent) {
				// DO NOTHING				
			}
		};
		
		Runtime rt = new ParallelRuntime();
		rt.init();

		for ( int COUNT : COUNTS) {
			long start = System.nanoTime();
			for (int i = 0; i < COUNT; i++) {
				@SuppressWarnings("unused")
				Task t = rt.createNonBlockingTask(body);
			}
			long end = System.nanoTime();

			String result = String.format("Created %10d tasks in %12d ns ==> %10d ns per task creation.", COUNT, (end-start), ((end-start)/COUNT));
			reporter.reportLn(result);
		}
		rt.shutdown();
	}
	
	@Override
	public String getName() {
		return name;
	}
}
