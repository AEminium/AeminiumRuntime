package aeminium.runtime.tools.benchmark.forkjoin;

import java.util.Random;

import jsr166y.ForkJoinPool;
import aeminium.runtime.examples.fjtests.AeminiumBFS;
import aeminium.runtime.examples.fjtests.Graph;
import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.BFS;

public class BFSBenchmarkSuite {
	
	Benchmark[] tests;
	
	protected int DEPTH = 23;
	protected int WIDTH = 2;
	protected int THRESHOLD = 21;
	protected int TARGET = 1;
	
	protected Graph input = Graph.randomIntGraph(DEPTH, WIDTH, new Random(1234567890));
	
	public BFSBenchmarkSuite() {
		tests = new Benchmark[3];
		
		tests[0] = new Benchmark() {
			@Override
			public String getName() {
				return "Sequential BFS";
			}
			
			@Override
			public long run() {
				long start = System.nanoTime();
				BFS.seqCount(TARGET, input);
				long end = System.nanoTime();
				
				return end-start;
			}
			
		};
		
		tests[1] = new Benchmark() {
			
			ForkJoinPool pool = new ForkJoinPool();
			@Override
			public String getName() {
				return "ForkJoin BFS";
			}
			
			@Override
			public long run() {
				BFS finder = new BFS(TARGET, input, THRESHOLD);
				long start = System.nanoTime();
				pool.invoke(finder);
				long end = System.nanoTime();
				
				return end-start;
			}
		};
		
		tests[2] = new Benchmark() {
			
			Runtime rt = Factory.getRuntime();
			
			@Override
			public String getName() {
				return "Aeminium BFS";
			}
			
			@Override
			public long run() {
				rt.init();
				Body searchBody = AeminiumBFS.createSearchBody(rt, TARGET, input, THRESHOLD);
				
				long start = System.nanoTime();
				Task t1 = rt.createNonBlockingTask(searchBody, Runtime.NO_HINTS);
				rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
				rt.shutdown();
				long end = System.nanoTime();
				return end-start;
			}
		};
		
	}
	
	
	public static void main(String[] args) {
		BFSBenchmarkSuite suite = new BFSBenchmarkSuite();
		new BenchmarkExecutor(suite.getTests()).run(args);
	}
	
	public Benchmark[] getTests() {
		return tests;
	}

}
