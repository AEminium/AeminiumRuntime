package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

import aeminium.runtime.ResultBody;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.examples.fjtests.AeminiumFibonacci;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.implementations.Factory.RuntimeConfiguration;
import aeminium.runtime.tools.benchmark.Reporter;

public class AeminiumFibonacciBenchmark extends FibonacciBenchmark {

	public static class FibBody implements ResultBody<Integer>, FibonacciConstants {
		private final Runtime rt;
		private final int n;
		private FibBody b1;
		private FibBody b2;
		public volatile int value = 0;
		
		FibBody(int n, Runtime rt) {
			this.n = n;
			this.rt = rt;
		}
		
		@Override
		public void completed() {
			if ( b1 != null && b2 != null ) {
				value = b1.value + b2.value;
			} else {
				value = 1;
			}
			b1 = null;
			b2 = null;
		}

		
		public int seqFib(int n) {
			if (n <= 2) return 1;
			else return (seqFib(n-1) + seqFib(n-2));
		}
		
		@Override
		public void execute(Task current) {
			if ( THRESHOLD < n ) {
				b1 = new FibBody(n-1, rt);
				Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
				rt.schedule(t1, current, Runtime.NO_DEPS);
				
				b2 = new FibBody(n-2, rt);
				Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
				rt.schedule(t2, current, Runtime.NO_DEPS);
			} else {
				seqFib(n);
			}
		}
	}

	
	public void runTest(Runtime rt, String version, EnumSet<Flags> flags, Reporter reporter, int n) {
		long start = System.nanoTime();
		rt.init();
		Task t1 = rt.createNonBlockingTask(AeminiumFibonacci.createFibBody(rt, n),
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
		for (String runtimeName : impls.keySet()) {
			Runtime rt = impls.get(runtimeName).instanciate(flags);
			for (String temperature : Arrays.asList("Cold", "Warm")) {
				String reportName = String.format("Aeminium %s %s %s", runtimeName,
						flags, temperature);
				reporter.startBenchmark(reportName);
				runTest(rt, runtimeName, flags, reporter, MAX_CALC);

				reporter.stopBenchmark(reportName);
			}
		}
	}

}
