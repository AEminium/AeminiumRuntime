package aeminium.runtime.tools.benchmark.forkjoin;

import jsr166y.ForkJoinPool;
import aeminium.runtime.examples.fjtests.AeminiumFFT;
import aeminium.runtime.examples.fjtests.Complex;
import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.FFT;

public class FFTBenchmarkSuite {
	
	Benchmark[] tests;
	
	protected int PARAMETER = 1048576;
	protected int THRESHOLD = 32768;
	
	protected Complex[] input = AeminiumFFT.createRandomComplexArray(PARAMETER, 1234567890);
	
	public FFTBenchmarkSuite() {
		tests = new Benchmark[3];
		
		tests[0] = new Benchmark() {
			@Override
			public String getName() {
				return "Sequential FFT";
			}
			
			@Override
			public long run() {
				long start = System.nanoTime();
				AeminiumFFT.sequentialFFT(input);
				long end = System.nanoTime();
				
				return end-start;
			}
			
		};
		
		tests[1] = new Benchmark() {
			
			ForkJoinPool pool = new ForkJoinPool();
			@Override
			public String getName() {
				return "ForkJoin FFT";
			}
			
			@Override
			public long run() {
				FFT fft = new FFT(input, THRESHOLD);
				long start = System.nanoTime();
				pool.invoke(fft);
				long end = System.nanoTime();
				
				return end-start;
			}
		};
		
		tests[2] = new Benchmark() {
			
			Runtime rt = Factory.getRuntime();
			
			@Override
			public String getName() {
				return "Aeminium FFT";
			}
			
			@Override
			public long run() {
				rt.init();
				Body fftBody = AeminiumFFT.createFFTBody(rt, input, THRESHOLD);
				
				long start = System.nanoTime();
				Task t1 = rt.createNonBlockingTask(fftBody, Runtime.NO_HINTS);
				rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
				rt.shutdown();
				long end = System.nanoTime();
				return end-start;
			}
		};
		
	}
	
	
	public static void main(String[] args) {
		FFTBenchmarkSuite suite = new FFTBenchmarkSuite();
		new BenchmarkExecutor(suite.getTests()).run(args);
	}
	
	public Benchmark[] getTests() {
		return tests;
	}

}
