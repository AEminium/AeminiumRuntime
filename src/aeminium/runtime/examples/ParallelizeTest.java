package aeminium.runtime.examples;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class ParallelizeTest {
	public static class FibBody implements Body {
		public volatile int value;
		private int threshold;

		public FibBody(int n, int threshold) {
			this.value = n;
			this.threshold = threshold;
		}

		public int seqFib(int n) {
			if (n <= 2) return 1;
			else return (seqFib(n - 1) + seqFib(n - 2));
		}

		@Override
		public void execute(Runtime rt, Task current) {
			value = fib(rt, current, value);
		}

		protected final int fib(Runtime rt, Task current, int value) {
			if ( rt.parallelize() ) {
				if ( value <= threshold ) {
					return seqFib(value);
				} else { 
					FibBody b1 = new FibBody(value - 1, threshold);
					Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
					rt.schedule(t1, current, Runtime.NO_DEPS);

					FibBody b2 = new FibBody(value - 2, threshold);
					Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
					rt.schedule(t2, current, Runtime.NO_DEPS);

					t1.getResult();
					t2.getResult();
					return  b1.value + b2.value;
				}
			} else {
				if ( value <= threshold ) {
					return seqFib(value);
				} else {
					return fib(rt,current,value-1) + fib(rt,current,value-2);
				} 
			}
		}
	}

	public static Body createFibBody(final Runtime rt, final int n, int threshold) {
		return new  ParallelizeTest.FibBody(n, threshold);
	}

	public static void main(String[] args) {
		final int N = 40;
		Runtime rt = Factory.getRuntime();
		rt.init();
		FibBody body = new ParallelizeTest.FibBody(N, 2);
		Task t1 = rt.createNonBlockingTask(body, Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.shutdown();

		System.out.println("F(" + N + ") = " + body.value);
	}
}
