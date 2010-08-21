package aeminium.runtime.tools.benchmark.forkjoin;

import jsr166y.ForkJoinPool;
import aeminium.runtime.examples.fjtests.AeminiumFibonacci;
import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.Fibonacci;

public class FibonacciBenchmarkSuite {
	
	Benchmark[] tests;
	
	protected int PARAMETER = 46;
	protected int THRESHOLD = 22;
	
	public FibonacciBenchmarkSuite() {
		tests = new Benchmark[3];
		
		tests[0] = new Benchmark() {
			@Override
			public String getName() {
				return "Sequential Fibonacci";
			}
			
			@Override
			public long run() {
				
				long start = System.nanoTime();
				seqFib(PARAMETER);
				long end = System.nanoTime();
				
				return end-start;
			}
			public int seqFib(int n) {
				return (n <= 2) ? 1 : seqFib(n-1) + seqFib(n-2);
			}
			
		};
		
		tests[1] = new Benchmark() {
			
			ForkJoinPool pool = new ForkJoinPool();
			@Override
			public String getName() {
				return "ForkJoin Fibonacci";
			}
			
			@Override
			public long run() {
				Fibonacci fib = new Fibonacci(PARAMETER, THRESHOLD);
				long start = System.nanoTime();
				pool.invoke(fib);
				long end = System.nanoTime();
				
				return end-start;
			}
		};
		
		tests[2] = new Benchmark() {
			
			Runtime rt = Factory.getRuntime();
			
			@Override
			public String getName() {
				return "Aeminium Fibonacci";
			}
			
			@Override
			public long run() {

				rt.init();
				Body fibBody = AeminiumFibonacci.createFibBody(rt, PARAMETER, THRESHOLD);
				
				long start = System.nanoTime();
				Task t1 = rt.createNonBlockingTask(fibBody, Runtime.NO_HINTS);
				rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
				
				rt.shutdown();
				long end = System.nanoTime();
				return end-start;
			}
		};
		
	}
	
	
	public static void main(String[] args) {
		FibonacciBenchmarkSuite suite = new FibonacciBenchmarkSuite();
		new BenchmarkExecutor(suite.getTests()).run();
	}
	
	public Benchmark[] getTests() {
		return tests;
	}

}
