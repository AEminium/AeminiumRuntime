package aeminium.runtime.tools.benchmark.forkjoin;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.examples.fjtests.AeminiumFib;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.implementations.Factory.RuntimeConfiguration;
import aeminium.runtime.tools.benchmark.Reporter;

public class AeminiumFibonacciBenchmark extends FibonacciBenchmark {

	public void runTest(Runtime rt, String version, EnumSet<Flags> flags,
			Reporter reporter, int n) {
		long start = System.nanoTime();
		rt.init();
		Task t1 = rt.createNonBlockingTask(AeminiumFib.createFibBody(rt, n),
				Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.shutdown();

		long end = System.nanoTime();

		String result = String.format("%d", (end - start));
		reporter.reportLn(result);
		reporter.flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		Map<String, RuntimeConfiguration> impls = Factory
				.getImplementations();
		//for (String runtimeName : impls.keySet()) {
		for (String runtimeName :  Arrays.asList("default") ) {
			Runtime rt = impls.get(runtimeName).instanciate(flags);
			for (String temperature : Arrays.asList("Cold", "Warm")) {
				String reportName = String.format("Aeminium %s %s %s", runtimeName,
						flags, temperature);
				reporter.startBenchmark(reportName);
				reporter.reportLn("Values in nanoseconds.");
				runTest(rt, runtimeName, flags, reporter, MAX_CALC);
				/*for (int i = 1; i <= MAX_CALC; i++) {
					runTest(rt, runtimeName, flags, reporter, i);
				}*/
				reporter.stopBenchmark(reportName);
			}
		}
	}

}
