package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import jsr166y.ForkJoinPool;
import jsr166y.RecursiveAction;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.tools.benchmark.Reporter;

public class ForkJoinFibonacciBenchmark extends FibonacciBenchmark {

	public static class FibonacciAction extends RecursiveAction { 
		private static final long serialVersionUID = 4892303970124587627L;
		public volatile int number;		
		
		public FibonacciAction(int n) { number = n; }

		public int seqFib(int n) {
			if (n <= 2) {
				return 1;
			} else {
				return seqFib(n-1) + seqFib(n-2);
			}
		}
		
		@Override
		protected final void compute() {
			int n = number;
			if (n <= 1) { 
				/* do nothing */ 
			} else if (n <= THRESHOLD) { 
				number = seqFib(n);
			} else {
				FibonacciAction f1 = new FibonacciAction(n - 1);	
				FibonacciAction f2 = new FibonacciAction(n - 2);
				invokeAll(f1,f2);
				number = f1.number + f2.number; // compose
			}
		}
	}
	
	protected long runTest(int n) {
		long start = System.nanoTime();
		ForkJoinPool pool = new ForkJoinPool();
		
		FibonacciAction task = new FibonacciAction(n);
		pool.invoke(task);

		long end = System.nanoTime();
		return (end-start);
	}

	@Override
	public void run(Reporter reporter) {
		long cold = runTest(MAX_CALC);
		long warm = runTest(MAX_CALC);
		reporter.reportLn(String.format(RESULT_FORMAT, Configuration.getProcessorCount(), cold, warm));
	}

}
