package aeminiumruntime.tools.benchmark;

public class RTBench {
	
	private static IBenchmark[] benchmarks = {
		new TaskCreationBenchmark(),
		new IndependentTaskGraph(),
		new LinearTaskGraph(),
		new FixedParallelMaxDependencies(),
		new ChildTaskBenchmark()
	};
	
	public static void main(String[] args) {
		System.out.println("===============================================================");
		System.out.println("==              AEminium Runtime Benchmark                   ==");
		System.out.println("===============================================================");
		
		IReporter reporter = new StringBuilderReporter();
		
		for ( IBenchmark benchmark : benchmarks ) {
			reporter.startBenchmark(benchmark.getName());
			benchmark.run(reporter);
			reporter.stopBenchmark(benchmark.getName());
			reporter.flush();
		}
		reportVMStats(reporter);
		reporter.flush();
	}
	
	private static void reportVMStats(IReporter reporter) {
		reporter.reportLn(String.format("Memory (TOTAL/MAX/FREE) (%d,%d,%d)", Runtime.getRuntime().totalMemory(),
																 			  Runtime.getRuntime().maxMemory(),
																 			  Runtime.getRuntime().freeMemory()));
	}
}
