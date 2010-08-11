package aeminium.runtime.tools.benchmark.forkjoin.integrate;

import java.util.Arrays;

import aeminium.runtime.tools.benchmark.Reporter;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.Integrate.SQuad;

public class SequentialIntegrateBenchmark extends IntegrateBenchmark {

	void runTest(Reporter reporter) {
		long start = System.nanoTime();
		
		SQuad seq = new SQuad(START, END, 0);
		seq.compute();
		
		long end = System.nanoTime();
		
		String result = String.format("%d", (end-start));
		reporter.reportLn(result);
		reporter.flush();
	}

	@Override
	public void run(Reporter reporter) {
		String reportName = "Sequential Version";
		reporter.startBenchmark(reportName);
		for (String temperature : Arrays.asList("Cold", "Warm")) {
			reporter.startBenchmark(reportName + " " + temperature);
			runTest(reporter);
			reporter.stopBenchmark(reportName);
		}
	}
}
