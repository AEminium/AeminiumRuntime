package aeminium.runtime.tools.benchmark.forkjoin.logcounter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.implementations.Factory.RuntimeConfiguration;
import aeminium.runtime.tools.benchmark.Reporter;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.LogCounter;

public class AeminiumLogCounterBenchmark extends LogCounterBenchmark {

	public void runTest(Runtime rt, String version, EnumSet<Flags> flags,
			Reporter reporter) {
		
		long start = System.nanoTime();
		int n = LogCounter.aeminiumCounter(dirpath, rt);
		long end = System.nanoTime();
		
		assert(n == 700405);
		
		String result = String.format("%d", (end - start));
		reporter.reportLn(result);
		reporter.flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		Map<String, RuntimeConfiguration> impls = Factory
		.getImplementations();
for (String runtimeName : impls.keySet()) {
	Runtime rt = impls.get(runtimeName).instanciate(flags);
	for (String temperature : Arrays.asList("Cold", "Warm")) {
		String reportName = String.format("Aeminium %s %s %s", runtimeName,
				flags, temperature);
		reporter.startBenchmark(reportName);
		runTest(rt, runtimeName, flags, reporter);

		reporter.stopBenchmark(reportName);
	}
}
	}
}
