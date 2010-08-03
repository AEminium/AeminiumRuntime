package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import aeminium.runtime.tools.benchmark.Benchmark;
import aeminium.runtime.tools.benchmark.forkjoin.BenchmarkExecutor;
import aeminium.runtime.tools.benchmark.forkjoin.fibonacci.AeminiumFibonacciBenchmark;
import aeminium.runtime.tools.benchmark.forkjoin.fibonacci.ForkJoinFibonacciBenchmark;
import aeminium.runtime.tools.benchmark.forkjoin.fibonacci.SequentialFibonacciBenchmark;

public class FibonacciBenchmarkExecutor extends BenchmarkExecutor {

	public Benchmark[] getBenchmarks() {
		Benchmark[] tests = {
			new AeminiumFibonacciBenchmark(),
			//new ForkJoinFibonacciBenchmark(),
			//new SequentialFibonacciBenchmark()
		};
		return tests;
	}
	
	public static void main(String[] args) {
		BenchmarkExecutor bench = new FibonacciBenchmarkExecutor();
		bench.run();
	}

	@Override
	public String getVersion() {
		return "Fibonacci Test (Values in nanoseconds.)";
	}
}
