package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import java.util.Arrays;
import java.util.EnumSet;

import jsr166y.ForkJoinPool;


import aeminium.runtime.implementations.Flags;
import aeminium.runtime.tools.benchmark.Reporter;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.Fibonacci;

public class ForkJoinFibonacciBenchmark extends FibonacciBenchmark {

	void runTest(Reporter reporter, int n) {
		ForkJoinPool pool = new ForkJoinPool();
		long start = System.nanoTime();
		
		Fibonacci task = new Fibonacci(n);
		pool.invoke(task);

		long end = System.nanoTime();
		
		String result = String.format("%d", (end-start));
		reporter.reportLn(result);
		reporter.flush();
	}

	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		String reportName = "ForkJoin Version";
		for (String temperature : Arrays.asList("Cold", "Warm")) {
			reporter.startBenchmark(reportName + " " + temperature);
			runTest(reporter, MAX_CALC);
			reporter.stopBenchmark(reportName);
		}
	}

}
