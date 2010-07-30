package aeminium.runtime.tools.benchmark.forkjoin.integrate;

import java.util.Arrays;
import java.util.EnumSet;

import jsr166y.ForkJoinPool;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.tools.benchmark.Reporter;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.Integrate.FQuad;

public class ForkJoinIntegrateBenchmark extends IntegrateBenchmark {

	void runTest(Reporter reporter) {
		
		ForkJoinPool g = new ForkJoinPool();
		 
		long start = System.nanoTime();
		
		FQuad seq = new FQuad(START, END, 0);
		g.invoke(seq);
		
		long end = System.nanoTime();
		
		String result = String.format("%d", (end-start));
		reporter.reportLn(result);
		reporter.flush();
	}

	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		String reportName = "ForkJoin Version";
		reporter.startBenchmark(reportName);
		for (String temperature : Arrays.asList("Cold", "Warm")) {
			reporter.startBenchmark(reportName + " " + temperature);
			runTest(reporter);
			reporter.stopBenchmark(reportName);
		}
	}
}
