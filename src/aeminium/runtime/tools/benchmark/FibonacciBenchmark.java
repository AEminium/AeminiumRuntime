package aeminium.runtime.tools.benchmark;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;


class FibBody implements Body {
	public volatile int value;
	
	FibBody(int n) {
		value = n;
	}

	@Override
	public final void execute(Runtime rt, Task current) {
		if ( 2 < value ) {
			FibBody b1 = new FibBody(value-1);
			Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
			rt.schedule(t1, current, Runtime.NO_DEPS);
			
			FibBody b2 = new FibBody(value-2);
			Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
			rt.schedule(t2, current, Runtime.NO_DEPS);
			
			t1.getResult();
			t2.getResult();
			value = b1.value + b2.value;
		} else {
			value = 1;
		}
	}

	@Override
	public String toString() {
		return "FibBody("+value+")";
	}
}


public class FibonacciBenchmark implements Benchmark {
	private static final String name = "FibonacciBenchmark";
	private int[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29};

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run(Reporter reporter) {
		for (int level : input) {
			runTest(reporter, level);
		}
	}

	public void runTest(Reporter reporter, int n) {
		Runtime rt = Factory.getRuntime();
		long start = System.nanoTime();
		rt.init();

		FibBody rootBody = new FibBody(n);
		Task root = rt.createNonBlockingTask(rootBody, Runtime.NO_HINTS);
		rt.schedule(root, Runtime.NO_PARENT, Runtime.NO_DEPS);

		rt.shutdown();  

		long end = System.nanoTime();

		String result = String.format("Fib(%3d) =  %7d in %12d ns", n, rootBody.value, (end-start));
		reporter.reportLn(result);
		reporter.flush();

	}
}

