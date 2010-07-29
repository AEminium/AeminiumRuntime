package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import java.util.Arrays;
import java.util.EnumSet;


import aeminium.runtime.implementations.Flags;
import aeminium.runtime.tools.benchmark.Reporter;

public class SequentialFibonacciBenchmark extends FibonacciBenchmark {

	void runTest(Reporter reporter, int n) {
		long start = System.nanoTime();
		
		SequentialFibonacciBenchmark.fib(n);

		long end = System.nanoTime();
		
		String result = String.format("%d", (end-start));
		reporter.reportLn(result);
		reporter.flush();
	}

	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		String reportName = "Sequential Version";
		reporter.startBenchmark(reportName);
		for (String temperature : Arrays.asList("Cold", "Warm")) {
			reporter.startBenchmark(reportName + " " + temperature);
			runTest(reporter, MAX_CALC);
			reporter.stopBenchmark(reportName);
		}
	}
	
	private static int fib(int n) {
		if (n <= 2) {
			return 1;
		} else {
			return fib(n-1) + fib(n-2);
		}
	}

}
