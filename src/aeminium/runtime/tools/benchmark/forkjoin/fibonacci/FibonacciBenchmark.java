package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.tools.benchmark.Benchmark;
import aeminium.runtime.tools.benchmark.Reporter;

public abstract class FibonacciBenchmark implements Benchmark, FibonacciConstants {

	@Override
	public String getName() {
		return "Fibonacci Benchmark";
	}

	@Override
	public abstract void run(String version, EnumSet<Flags> flags, Reporter reporter);
	

}
