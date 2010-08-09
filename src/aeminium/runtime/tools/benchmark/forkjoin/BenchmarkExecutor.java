package aeminium.runtime.tools.benchmark.forkjoin;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.tools.benchmark.Benchmark;
import aeminium.runtime.tools.benchmark.StringBuilderReporter;

public abstract class BenchmarkExecutor {
	
	protected void usage() {
		System.out.println();
		System.out.println("java " + this.getClass().getCanonicalName() + " COMMAND");
		System.out.println("");
		System.out.println("COMMANDS:");
		System.out.println(" list             - List available benchmarks.");
		System.out.println(" run BENCHMARK    - Run specified benchmark.");
		System.out.println();
	}
	
	public void run(String[] args) {
		
		if ( args.length == 0 ) {
			usage();
			return;
		}
		
		if ( args[0].equals("list")) {
			for ( Benchmark entry : getBenchmarks() ) {
				System.out.println(entry.getName());
			}
		} else if ( args[0].equals("run") && args.length == 2 ) {
			Benchmark benchmark = null;
			
			for ( Benchmark entry : getBenchmarks() ) {
				if ( entry.getName().equals(args[1]) ) {
					benchmark = entry;
					break;
				}
			}
			
			if ( benchmark != null ) {
				//System.out.println("run " + benchmark.getName());
				StringBuilderReporter reporter = new StringBuilderReporter();
				reporter.startBenchmark(benchmark.getName());
				reporter.reportLn("#       CPU#         COLD         WARM");
				benchmark.run(Configuration.getImplementation(), Configuration.getFlags(), reporter);
				reporter.stopBenchmark(benchmark.getName());
				reporter.flush();
			} else {
				usage();
				return;
			}
		} else {
			usage();
			return;
		}
//		Reporter reporter = new SimpleStringBuilderReporter();
//		reporter.reportLn(String.format("Number of cores used: %d", Runtime.getRuntime().availableProcessors()));
//		
//		String version = getVersion();
//		
//		for ( Benchmark benchmark : getBenchmarks() ) {
//			reporter.startBenchmark(benchmark.getName());
//			benchmark.run(version, Factory.getFlagsFromEnvironment(), reporter);
//			reporter.stopBenchmark(benchmark.getName());
//			reporter.flush();
//			//reportVMStats(reporter);
//		}
//		
//		reporter.flush();
	}

	public abstract Benchmark[] getBenchmarks();

	public abstract String getVersion();

}
