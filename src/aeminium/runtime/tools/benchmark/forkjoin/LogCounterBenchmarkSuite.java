package aeminium.runtime.tools.benchmark.forkjoin;

import java.io.IOException;

import jsr166y.ForkJoinPool;
import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.LogCounter;

public class LogCounterBenchmarkSuite {
	
	Benchmark[] tests;
	String sep = System.getProperty("file.separator");
	String dirpath = System.getProperty("user.home") + sep + "Desktop" + sep + "logs" + sep + "apache2" + sep; 
	
	public LogCounterBenchmarkSuite() {
		tests = new Benchmark[3];
		
		tests[0] = new Benchmark() {
			@Override
			public String getName() {
				return "Sequential LogCounter";
			}
			
			@Override
			public long run() {
				cleanUp();
				
				long start = System.nanoTime();
				int n = LogCounter.sequentialCounter(dirpath);
				long end = System.nanoTime();
				assert(n == 700405);
				return end-start;
			}
		};
		
		tests[1] = new Benchmark() {
			
			ForkJoinPool pool = new ForkJoinPool();
			
			@Override
			public String getName() {
				return "ForkJoin LogCounter";
			}
			
			@Override
			public long run() {
				
				long start = System.nanoTime();
				int n = LogCounter.forkjoinCounter(dirpath, pool);
				long end = System.nanoTime();
				assert(n == 700405);
				return end-start;
			}
		};
		
		tests[2] = new Benchmark() {
			
			Runtime rt = Factory.getRuntime();
			
			@Override
			public String getName() {
				return "Aeminium Logcounter";
			}
			
			@Override
			public long run() {

				long start = System.nanoTime();
				int n = LogCounter.aeminiumCounter(dirpath, rt);
				long end = System.nanoTime();
				assert(n == 700405);
				return end-start;
			}
		};
		
	}
	
	protected void cleanUp() {
		try {
			LogCounter.cleanFiles(dirpath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		LogCounterBenchmarkSuite suite = new LogCounterBenchmarkSuite();
		new BenchmarkExecutor(suite.getTests()).run(args);
	}
	
	public Benchmark[] getTests() {
		return tests;
	}

}
