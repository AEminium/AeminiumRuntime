package aeminium.runtime.tools.benchmark.forkjoin.logcounter;

import java.util.Arrays;

import jsr166y.ForkJoinPool;
import aeminium.runtime.tools.benchmark.Reporter;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.LogCounter;

public class ForkJoinLogCounterBenchmark extends LogCounterBenchmark {

	void runTest(Reporter reporter) {
		
		ForkJoinPool pool = new ForkJoinPool();
		 
		long start = System.nanoTime();
		
		int n = LogCounter.forkjoinCounter(dirpath, pool);
		
		long end = System.nanoTime();
		
		assert(n == 700405);
		String result = String.format("%d", (end-start));
		reporter.reportLn(result);
		reporter.flush();
	}

	@Override
	public void run(Reporter reporter) {
		String reportName = "ForkJoin Version";
		reporter.startBenchmark(reportName);
		for (String temperature : Arrays.asList("Cold", "Warm")) {
			reporter.startBenchmark(reportName + " " + temperature);
			runTest(reporter);
			reporter.stopBenchmark(reportName);
		}
	}
}
