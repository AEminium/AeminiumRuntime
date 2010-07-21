package aeminium.runtime.tools.benchmark;

import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flag;


public class TaskCreationBenchmark implements Benchmark {
	private final String name = "TaskCreation";
	private final int[] COUNTS = { 100, 1000, 10000, 100000, 1000000};
	
	@Override
	public void run(String version, EnumSet<Flag> flags, Reporter reporter) {
		Body body = new Body() {
			@Override
			public void execute(Task parent) {
				// DO NOTHING				
			}
		};

		Runtime rt = Factory.getRuntime(version, flags);
		rt.init();

		for ( int COUNT : COUNTS) {
			long start = System.nanoTime();
			for (int i = 0; i < COUNT; i++) {
				@SuppressWarnings("unused")
				Task t = rt.createNonBlockingTask(body, Runtime.NO_HINTS);
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
