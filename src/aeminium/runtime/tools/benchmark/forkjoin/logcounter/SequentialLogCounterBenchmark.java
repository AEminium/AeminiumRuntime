package aeminium.runtime.tools.benchmark.forkjoin.logcounter;

import java.util.Arrays;
import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.tools.benchmark.Reporter;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.LogCounter;

public class SequentialLogCounterBenchmark extends LogCounterBenchmark {

	void runTest(Reporter reporter) {
		
		cleanUp();
		
		long start = System.nanoTime();
		
		int n = LogCounter.sequentialCounter(dirpath);
		
		long end = System.nanoTime();
		
		assert(n == 700405);
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
