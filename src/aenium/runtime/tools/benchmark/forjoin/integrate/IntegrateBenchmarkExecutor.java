package aenium.runtime.tools.benchmark.forjoin.integrate;

import aeminium.runtime.tools.benchmark.Benchmark;
import aeminium.runtime.tools.benchmark.forkjoin.BenchmarkExecutor;

public class IntegrateBenchmarkExecutor extends BenchmarkExecutor {

	public Benchmark[] getBenchmarks() {
		Benchmark[] tests = {
			new AeminiumIntegrateBenchmark(),
			new ForkJoinIntegrateBenchmark(),
			new SequentialIntegrateBenchmark()
		};
		return tests;
	}
	
	public static void main(String[] args) {
		BenchmarkExecutor bench = new IntegrateBenchmarkExecutor();
		bench.run();
	}

	@Override
	public String getVersion() {
		return "Fibonacci Test (Values in nanoseconds.)";
	}
}
