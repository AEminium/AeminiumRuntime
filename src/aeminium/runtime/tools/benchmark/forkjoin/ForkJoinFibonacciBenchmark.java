package aeminium.runtime.tools.benchmark.forkjoin;

import java.util.EnumSet;

import jsr166y.ForkJoinPool;


import aeminium.runtime.implementations.Flags;
import aeminium.runtime.tools.benchmark.Reporter;

public class ForkJoinFibonacciBenchmark extends FibonacciBenchmark {

	void runTest(Reporter reporter, int n) {
		ForkJoinPool pool = new ForkJoinPool();
		long start = System.nanoTime();
		
		ForkJoinFibonacciImplementation task = new ForkJoinFibonacciImplementation(n);
		pool.invoke(task);

		long end = System.nanoTime();
		
		String result = String.format("%d", (end-start));
		reporter.reportLn(result);
		reporter.flush();
	}

	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		String reportName = "ForkJoin Version";
		reporter.startBenchmark(reportName);
		reporter.reportLn("Values in nanoseconds.");
		runTest(reporter, MAX_CALC);
		/*for (int i = 1; i <= MAX_CALC; i++) {
			runTest(reporter, i);	
		}*/
		reporter.stopBenchmark(reportName);
	}

}
