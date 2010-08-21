package aeminium.runtime.tools.benchmark.forkjoin;

public class BenchmarkExecutor {
	
	Benchmark[] tests;
	
	public BenchmarkExecutor(Benchmark[] benchs) {
		tests = benchs;
	}
	
	public void run() {
		for (Benchmark test: tests) {
			long cold = test.run();
			long warm = test.run();
			
			System.out.println(String.format("%s: %d %d", test.getName(), cold, warm));
		}
	}
}
