package aeminium.runtime.tools.benchmark.forkjoin;

import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.Benchmark;
import aeminium.runtime.tools.benchmark.RTBench;
import aeminium.runtime.tools.benchmark.Reporter;

public abstract class BenchmarkExecutor extends RTBench {
	
	public void run() {
		Reporter reporter = new SimpleStringBuilderReporter();
		reporter.reportLn(String.format("Number of cores used: %d", Runtime.getRuntime().availableProcessors()));
		
		String version = getVersion();
		
		for ( Benchmark benchmark : getBenchmarks() ) {
			reporter.startBenchmark(benchmark.getName());
			benchmark.run(version, Factory.getFlagsFromEnvironment(), reporter);
			reporter.stopBenchmark(benchmark.getName());
			reporter.flush();
			reportVMStats(reporter);
		}
		
		reporter.flush();
	}

	public abstract Benchmark[] getBenchmarks();

	public abstract String getVersion();

}
