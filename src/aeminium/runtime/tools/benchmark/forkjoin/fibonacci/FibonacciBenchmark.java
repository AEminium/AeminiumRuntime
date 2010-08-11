package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import aeminium.runtime.tools.benchmark.Benchmark;
import aeminium.runtime.tools.benchmark.Reporter;

public abstract class FibonacciBenchmark implements Benchmark, FibonacciConstants {

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public abstract void run(Reporter reporter);
	

}
