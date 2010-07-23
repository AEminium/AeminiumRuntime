package aeminium.runtime.tools.benchmark.forkjoin;

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
		reporter.reportLn("Values in nanoseconds.");
		runTest(reporter, MAX_CALC);
		/*
		for (int i = 1; i <= MAX_CALC; i++) {
			runTest(reporter, i);	
		}*/
		reporter.stopBenchmark(reportName);
	}
	
	private static int fib(int n) {
		if (n <= 2) {
			return 1;
		} else {
			return fib(n-1) + fib(n-2);
		}
	}

}
