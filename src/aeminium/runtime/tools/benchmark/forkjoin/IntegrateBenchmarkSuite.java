package aeminium.runtime.tools.benchmark.forkjoin;

import jsr166y.ForkJoinPool;
import aeminium.runtime.examples.fjtests.AeminiumIntegrate;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.Integrate.FQuad;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.Integrate.SQuad;

public class IntegrateBenchmarkSuite {
	
	Benchmark[] tests;
	
	protected double START = -2101.0;
	protected double END = 200.0;
	
	public IntegrateBenchmarkSuite() {
		tests = new Benchmark[3];
		
		tests[0] = new Benchmark() {
			@Override
			public String getName() {
				return "Sequential Integrate";
			}
			
			@Override
			public long run() {
				
				long start = System.nanoTime();
				SQuad seq = new SQuad(START, END, 0);
				seq.compute();
				long end = System.nanoTime();
				
				return end-start;
			}
		};
		
		tests[1] = new Benchmark() {
			
			ForkJoinPool pool = new ForkJoinPool();
			@Override
			public String getName() {
				return "ForkJoin Integrate";
			}
			
			@Override
			public long run() {
				FQuad seq = new FQuad(START, END, 0);
				long start = System.nanoTime();
				pool.invoke(seq);
				long end = System.nanoTime();
				
				return end-start;
			}
		};
		
		tests[2] = new Benchmark() {
			
			Runtime rt = Factory.getRuntime();
			
			@Override
			public String getName() {
				return "Aeminium Integrate";
			}
			
			@Override
			public long run() {

				rt.init();
				long start = System.nanoTime();
				Task t1 = AeminiumIntegrate.recursiveCall(rt, Runtime.NO_PARENT, START, END, 0);
				rt.shutdown();
				long end = System.nanoTime();
				assert(t1.getResult() != null);
				return end-start;
			}
		};
		
	}
	
	
	public static void main(String[] args) {
		IntegrateBenchmarkSuite suite = new IntegrateBenchmarkSuite();
		new BenchmarkExecutor(suite.getTests()).run();
	}
	
	public Benchmark[] getTests() {
		return tests;
	}

}
