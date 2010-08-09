package aeminium.runtime.tools.benchmark;

import java.util.Arrays;

import aeminium.runtime.implementations.Configuration;
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
	
	public static void usage() {
		System.out.println();
		System.out.println("java aeminium.runtime.tools.benchmark.RTBench COMMAND");
		System.out.println("");
		System.out.println("COMMANDS:");
		System.out.println(" list             - List available benchmarks.");
		System.out.println(" run BENCHMARK    - Run specified benchmark.");
		System.out.println();
	}
	
	public static void main(String[] args) {
		

		if ( args.length == 0 ) {
			usage();
			return;
		}

		if ( args[0].equals("list") ) {
			System.out.println();
			System.out.println("#=================================================");
			System.out.println("# Available Benchmarks:");
			System.out.println("#=================================================");
			for( Benchmark benchmark : benchmarks ) {
				System.out.println(benchmark.getName());
			}
			System.out.println();
		} else if ( args[0].equals("run") && args.length == 2 ) {
			String version = "default";
			Benchmark benchmark = null;
			for ( Benchmark b : benchmarks ) {
				if ( b.getName().equals(args[1])) {
					benchmark = b;
				}
			}
			
			if ( benchmark != null ) {
				Reporter reporter = new StringBuilderReporter();
				reporter.startBenchmark(benchmark.getName());
				benchmark.run(version, Configuration.getFlags(), reporter);
				reporter.stopBenchmark(benchmark.getName());
				reporter.flush();
			} else {
				usage();
			}

		} else {
			usage();
		}
	}
	
	protected static void reportVMStats(Reporter reporter) {
		reporter.reportLn(String.format("Memory (TOTAL/MAX/FREE) (%d,%d,%d)", Runtime.getRuntime().totalMemory(),
																 			  Runtime.getRuntime().maxMemory(),
																 			  Runtime.getRuntime().freeMemory()));
	}
}
