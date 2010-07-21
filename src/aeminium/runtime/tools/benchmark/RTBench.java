package aeminium.runtime.tools.benchmark;

import aeminium.runtime.implementations.Factory;

public class RTBench {
	
	private static Benchmark[] benchmarks = {
		new TaskCreationBenchmark(),
		new IndependentTaskGraph(),
		new LinearTaskGraph(),
		new FixedParallelMaxDependencies(),
		new ChildTaskBenchmark(),
		new FibonacciBenchmark()
	};
	
	public static void main(String[] args) {
		System.out.println("===============================================================");
		System.out.println("==              AEminium Runtime Benchmark                   ==");
		System.out.println("===============================================================");
		String version = "default";
		
		Reporter reporter = new StringBuilderReporter();
		
		for ( Benchmark benchmark : benchmarks ) {
			reporter.startBenchmark(benchmark.getName());
			benchmark.run(version, Factory.getFlagsFromEnvironment(), reporter);
			reporter.stopBenchmark(benchmark.getName());
			reporter.flush();
		}
		reportVMStats(reporter);
		reporter.flush();
	}
	
	private static void reportVMStats(Reporter reporter) {
		reporter.reportLn(String.format("Memory (TOTAL/MAX/FREE) (%d,%d,%d)", Runtime.getRuntime().totalMemory(),
																 			  Runtime.getRuntime().maxMemory(),
																 			  Runtime.getRuntime().freeMemory()));
	}
}
