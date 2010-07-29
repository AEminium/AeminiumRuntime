package aeminium.runtime.tools.benchmark.forjoin.integrate;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.examples.fjtests.AeminiumIntegrate;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.implementations.Factory.RuntimeConfiguration;
import aeminium.runtime.tools.benchmark.Reporter;

public class AeminiumIntegrateBenchmark extends IntegrateBenchmark {

	public void runTest(Runtime rt, String version, EnumSet<Flags> flags,
			Reporter reporter) {
		
		long start = System.nanoTime();
		rt.init();
		Task t1 = AeminiumIntegrate.recursiveCall(rt, Runtime.NO_PARENT, START, END, 0);
		rt.shutdown();
		long end = System.nanoTime();
		
		assert(t1.getResult() != null);
		
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
