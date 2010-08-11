package aeminium.runtime.tools.benchmark.forkjoin.logcounter;


import java.io.File;
import java.io.IOException;

import aeminium.runtime.tools.benchmark.Benchmark;
import aeminium.runtime.tools.benchmark.Reporter;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.LogCounter;

public abstract class LogCounterBenchmark implements Benchmark {

	protected String dirpath;
	String def = "/Users/alcides/Desktop/logs/apache2/";
	public LogCounterBenchmark() {
		try {
			dirpath = System.getenv("logdir");
			new File(dirpath);
		} catch (NullPointerException e) {
			dirpath = def;
		}
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
	
	@Override
	public String getName() {
		return "Integrate Benchmark";
	}

	@Override
	public abstract void run(Reporter reporter);

}
