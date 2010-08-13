package aeminium.runtime.tools.benchmark;

import aeminium.runtime.ResultBody;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;


class FibBody implements ResultBody {
	private final Runtime rt;
	private final int n;
	private FibBody b1;
	private FibBody b2;
	public volatile int value = 1;
	
	FibBody(int n, Runtime rt) {
		this.n = n;
		this.rt = rt;
	}
	
	@Override
	public final void completed() {
		if ( b1 != null && b2 != null ) {
			value = b1.value + b2.value;
			b1 = null;
			b2 = null;
		}
	}

	@Override
	public final void execute(Task current) {
		if ( 2 < n ) {
			b1 = new FibBody(n-1, rt);
			Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
			rt.schedule(t1, current, Runtime.NO_DEPS);
			
			b2 = new FibBody(n-2, rt);
			Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
			rt.schedule(t2, current, Runtime.NO_DEPS);
		}
	}

	@Override
	public String toString() {
		return "FibBody("+n+")";
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

		FibBody rootBody = new FibBody(n, rt);
		Task root = rt.createNonBlockingTask(rootBody, Runtime.NO_HINTS);
		rt.schedule(root, Runtime.NO_PARENT, Runtime.NO_DEPS);

		rt.shutdown();  

		long end = System.nanoTime();

		String result = String.format("Fib(%3d) =  %7d in %12d ns", n, rootBody.value, (end-start));
		reporter.reportLn(result);
		reporter.flush();

	}
}

