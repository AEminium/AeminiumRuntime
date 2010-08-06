package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import java.util.Arrays;
import java.util.EnumSet;

import jsr166y.ForkJoinPool;
import jsr166y.RecursiveAction;


import aeminium.runtime.implementations.Flags;
import aeminium.runtime.tools.benchmark.Reporter;

public class ForkJoinFibonacciBenchmark extends FibonacciBenchmark {

	public static class Fibonacci extends RecursiveAction { 
		private static final long serialVersionUID = 4892303970124587627L;
		public volatile int number;		
		
		public Fibonacci(int n) { number = n; }

		private int seqFib(int n) {
			if (n <= 2) return 1;
			else return seqFib(n-1) + seqFib(n-2);
		}

		@Override
		protected void compute() {
			int n = number;
			if (n <= 1) { /* do nothing */ }
			else if (n <= THRESHOLD) 
				number = seqFib(n);
			else {
				Fibonacci f1 = new Fibonacci(n - 1);	
				Fibonacci f2 = new Fibonacci(n - 2);
				invokeAll(f1,f2);
				number = f1.number + f2.number; // compose
			}
		}
		
	}
	
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
