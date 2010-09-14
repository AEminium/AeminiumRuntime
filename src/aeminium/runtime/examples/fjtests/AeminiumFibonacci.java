package aeminium.runtime.examples.fjtests;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class AeminiumFibonacci {

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
			if ( value <= threshold  ) {
				value = seqFib(value);
			} else {
				FibBody b1 = new FibBody(value - 1, threshold);
				Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
				rt.schedule(t1, current, Runtime.NO_DEPS);

				FibBody b2 = new FibBody(value - 2, threshold);
				Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
				rt.schedule(t2, current, Runtime.NO_DEPS);
				 
				t1.getResult();
				t2.getResult();
				value = b1.value + b2.value;
			} 
		}
	}

	public static Body createFibBody(final Runtime rt, final int n, int threshold) {
		return new  AeminiumFibonacci.FibBody(n, threshold);
	}

	public static void main(String[] args) {
		Runtime rt = Factory.getRuntime();
		rt.init();
		FibBody body = new AeminiumFibonacci.FibBody(6, 1);
		Task t1 = rt.createNonBlockingTask(body, Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.shutdown();
		
		System.out.println("F(6) = " + body.value);
	}
}
