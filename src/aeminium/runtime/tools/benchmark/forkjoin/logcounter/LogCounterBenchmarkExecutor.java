package aeminium.runtime.tools.benchmark.forkjoin.logcounter;

import aeminium.runtime.tools.benchmark.Benchmark;
import aeminium.runtime.tools.benchmark.forkjoin.BenchmarkExecutor;

public class LogCounterBenchmarkExecutor extends BenchmarkExecutor {

	public Benchmark[] getBenchmarks() {
		Benchmark[] tests = {
			new AeminiumLogCounterBenchmark(),
			new ForkJoinLogCounterBenchmark(),
			new SequentialLogCounterBenchmark()
		};
		return tests;
	}
	
	public static void main(String[] args) {
		BenchmarkExecutor bench = new LogCounterBenchmarkExecutor();
		bench.run(args);
	}

	@Override
	public String getVersion() {
		return "Integrate Test (Values in nanoseconds.)";
	}
}
