package aeminium.runtime.tools.benchmark.forkjoin;

import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.Benchmark;
import aeminium.runtime.tools.benchmark.RTBench;
import aeminium.runtime.tools.benchmark.Reporter;
import aeminium.runtime.tools.benchmark.StringBuilderReporter;

public class BenchmarkRunner extends RTBench {
	private static Benchmark[] benchmarks = {
		new AeminiumFibonacciBenchmark(),
		new ForkJoinFibonacciBenchmark(),
		new SequentialFibonacciBenchmark()
	};
	
	public static void main(String[] args) {
		System.out.println("===============================================================");
		System.out.println("==              AEminium Runtime Benchmark                   ==");
		System.out.println("===============================================================");
		String version = "default";
		
		Reporter reporter = new StringBuilderReporter();
		
		for ( Benchmark benchmark : benchmarks ) {
			reporter.startBenchmark(benchmark.getName());
			reporter.reportLn(String.format("Number of cores used: %d", Runtime.getRuntime().availableProcessors()));
			benchmark.run(version, Factory.getFlagsFromEnvironment(), reporter);
			reporter.stopBenchmark(benchmark.getName());
			reporter.flush();
			reportVMStats(reporter);
		}
		
		reporter.flush();
	}

}
