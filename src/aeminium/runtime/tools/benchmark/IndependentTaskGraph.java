package aeminium.runtime.tools.benchmark;

import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;

public class IndependentTaskGraph implements Benchmark {
	private final String name = "IndepenetTaskGraph";
	private final int[] COUNTS = {100, 1000, 10000, 100000, 1000000};
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		for (int COUNT : COUNTS) {
			runTest(version, flags, reporter, COUNT);
			reporter.flush();
		}
	}

	private void runTest(String version, EnumSet<Flags> flags, Reporter reporter, int count) {
		Runtime rt = Factory.getRuntime(version, flags);

		rt.init();
		
		long start = System.nanoTime();
		for(int i = 0; i < count; i++ ) {
			Task nextTask = createTask(rt);
			rt.schedule(nextTask, Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		rt.shutdown();
		long end = System.nanoTime();
		String result = String.format("Run %10d tasks in %12d ns ==> %10d ns per task | %6d tasks/second.", count, (end-start), ((end-start)/count), (1000000000/((end-start)/count)));
		reporter.reportLn(result);

	}
	
	private Task createTask(Runtime rt) {
		return rt.createNonBlockingTask(new Body() {
			
			@Override
			public void execute(Task parent) {
				// DO NOTHING
			}
		}, Runtime.NO_HINTS);
	}
}