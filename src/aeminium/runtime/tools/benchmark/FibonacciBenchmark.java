package aeminium.runtime.tools.benchmark;

import java.util.EnumSet;

import aeminium.runtime.ResultBody;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;


class FibBody implements ResultBody<Integer> {
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

	@Override
	public void execute(Task current) {
		if ( 2 < n ) {
			b1 = new FibBody(n-1, rt);
			Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
			rt.schedule(t1, current, Runtime.NO_DEPS);
			
			b2 = new FibBody(n-2, rt);
			Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
			rt.schedule(t2, current, Runtime.NO_DEPS);
		}
	}
}


public class FibonacciBenchmark implements Benchmark {
	private final String name = "FibonacciBenchmark";
	private int[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29};

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		for (int level : input) {
			runTest(version, flags, reporter, level);
		}
	}

	public void runTest(String version, EnumSet<Flags> flags, Reporter reporter, int n) {
		Runtime rt = Factory.getRuntime(version, flags);
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

