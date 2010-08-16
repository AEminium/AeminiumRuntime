package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.Reporter;

public class AeminiumFibonacciBenchmark extends FibonacciBenchmark {

	public static class FibBody implements Body {
		public volatile int value;
		
		FibBody(int n) {
			this.value = n;
		}
		
		public int seqFib(int n) {
			if (n <= 2) {
				return 1;
			} else {
				return seqFib(n-1) + seqFib(n-2);
			}
		}
		
		@Override
		public final void execute(Runtime rt, Task current) {
			if ( value <= THRESHOLD  ) {
				value = seqFib(value);
			} else {
				FibBody b1 = new FibBody(value-1);
				Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
				rt.schedule(t1, current, Runtime.NO_DEPS);

				FibBody b2 = new FibBody(value-2);
				Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
				rt.schedule(t2, current, Runtime.NO_DEPS);
				
				t1.getResult();
				t2.getResult();
				value = b1.value + b2.value;
			} 
		}
		
		@Override
		public String toString() {
			return "FibBody("+value+")";
		}
	}
	
	protected long runTest(Runtime rt, int n) {
		long start = System.nanoTime();
		rt.init();
		
		Task t1 = rt.createNonBlockingTask(new FibBody(n), Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		
		rt.shutdown();

		long end = System.nanoTime();
		return (end - start);
	}

	@Override
	public void run(Reporter reporter) {
		Runtime rt = Factory.getRuntime();
		long cold = runTest(rt, MAX_CALC);
		long warm = runTest(rt, MAX_CALC);
		reporter.reportLn(String.format(RESULT_FORMAT, Configuration.getProcessorCount(), cold, warm));
	}

}
