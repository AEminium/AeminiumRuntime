package aeminium.runtime.tools.benchmark.forkjoin.integrate;

import java.util.Arrays;

import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.examples.fjtests.AeminiumIntegrate;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.Reporter;

public class AeminiumIntegrateBenchmark extends IntegrateBenchmark {

	public void runTest(Runtime rt, Reporter reporter) {
		
		long start = System.nanoTime();
		rt.init();
		Task t1 = AeminiumIntegrate.recursiveCall(rt, Runtime.NO_PARENT, START, END, 0);
		rt.shutdown();
		long end = System.nanoTime();
		
		assert(t1.getResult() != null);
		
		String result = String.format("%d", (end - start));
		reporter.reportLn(result);
		reporter.flush();
	}

	@Override
	public void run(Reporter reporter) {
		Runtime rt = Factory.getRuntime();
		for (String temperature : Arrays.asList("Cold", "Warm")) {
			String reportName = String.format("Aeminium %s",  temperature);
			reporter.startBenchmark(reportName);
			runTest(rt, reporter);

			reporter.stopBenchmark(reportName);

		}
	}
}