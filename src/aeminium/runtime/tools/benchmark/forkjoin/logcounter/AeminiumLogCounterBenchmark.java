package aeminium.runtime.tools.benchmark.forkjoin.logcounter;

import java.util.Arrays;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.Reporter;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.LogCounter;

public class AeminiumLogCounterBenchmark extends LogCounterBenchmark {

	public void runTest(Runtime rt, Reporter reporter) {
		
		long start = System.nanoTime();
		int n = LogCounter.aeminiumCounter(dirpath, rt);
		long end = System.nanoTime();
		
		assert(n == 700405);
		
		String result = String.format("%d", (end - start));
		reporter.reportLn(result);
		reporter.flush();
	}

	@Override
	public void run(Reporter reporter) {

		Runtime rt = Factory.getRuntime();
		for (String temperature : Arrays.asList("Cold", "Warm")) {
			String reportName = String.format("Aeminium %s", temperature);
			reporter.startBenchmark(reportName);
			runTest(rt, reporter);
			reporter.stopBenchmark(reportName);
		}
	}
}
