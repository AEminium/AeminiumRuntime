package aenium.runtime.tools.benchmark.forjoin.integrate;

import java.util.Arrays;
import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;
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
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		String reportName = "Sequential Version";
		reporter.startBenchmark(reportName);
		for (String temperature : Arrays.asList("Cold", "Warm")) {
			reporter.startBenchmark(reportName + " " + temperature);
			runTest(reporter);
			reporter.stopBenchmark(reportName);
		}
	}
}
