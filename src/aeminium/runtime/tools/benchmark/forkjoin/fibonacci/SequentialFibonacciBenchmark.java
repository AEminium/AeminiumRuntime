package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.tools.benchmark.Reporter;

public class SequentialFibonacciBenchmark extends FibonacciBenchmark {

	public int seqFib(int n) {
		if (n <= 2) {
			return 1;
		} else {
			return seqFib(n-1) + seqFib(n-2);
		}
	}
	
	protected long runTest(int n) {
		long start = System.nanoTime();
		
		seqFib(n);

		long end = System.nanoTime();
		return (end-start);
	}

	@Override
	public void run(Reporter reporter) {
		final long cold = runTest(MAX_CALC);
		final long warm = runTest(MAX_CALC);
		reporter.reportLn(String.format(RESULT_FORMAT, Configuration.getProcessorCount(), cold, warm));
	}
}
