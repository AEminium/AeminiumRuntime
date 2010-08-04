package aeminium.runtime.examples.fjtests;

import aeminium.runtime.Body;
import aeminium.runtime.ResultBody;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class AeminiumFibonacci {

	private static final int MAX_CALC = 46;
	private static final int THRESHOLD = 23;

	public static class FibBody implements ResultBody<Integer> {
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

	public static Body createFibBody(final Runtime rt, final int n) {
		return new  AeminiumFibonacci.FibBody(n, rt);
	}

	public static void main(String[] args) {
		Runtime rt = Factory.getRuntime();
		rt.init();

		Task t1 = rt.createNonBlockingTask(new  AeminiumFibonacci.FibBody(MAX_CALC, rt), Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.shutdown();
	}
}
