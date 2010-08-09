package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import java.util.EnumSet;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.tools.benchmark.Reporter;

public class SequentialFibonacciBenchmark extends FibonacciBenchmark {

	protected long runTest(int n) {
		long start = System.nanoTime();
		
		Fibonacci.fibOf(n);

		long end = System.nanoTime();
		return (end-start);
	}

	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		long cold = runTest(MAX_CALC);
		long warm = runTest(MAX_CALC);
		reporter.reportLn(String.format(RESULT_FORMAT, Configuration.getProcessorCount(), cold, warm));
	}
}
