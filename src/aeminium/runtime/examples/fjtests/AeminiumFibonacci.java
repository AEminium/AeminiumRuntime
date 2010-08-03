package aeminium.runtime.examples.fjtests;

import aeminium.runtime.Body;
import aeminium.runtime.ResultBody;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

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
//		return new Body() {
//			
//			public int seqFib(int n) {
//				if (n <= 2) return 1;
//				else return (seqFib(n-1) + seqFib(n-2));
//			}
//			
//			@Override
//			public void execute(final Task current) {
//				//System.out.println("n="+n);
//				if ( THRESHOLD < n ) {
//					final Task f1  = rt.createNonBlockingTask(createFibBody(rt, n-1), Runtime.NO_HINTS);
//					rt.schedule(f1, current, Runtime.NO_DEPS);
//					
//					final Task f2  = rt.createNonBlockingTask(createFibBody(rt, n-2), Runtime.NO_HINTS);
//					rt.schedule(f2, current, Runtime.NO_DEPS);
//					
//					current.setResult(new Result() {
//						@Override
//						public Object result() {
//							Integer v1 = (Integer)((Result)f1.getResult()).result();
//							Integer v2 = (Integer)((Result)f2.getResult()).result();
//							return v1 + v2;
//						}
//					});
//				} else {
//					final Integer result = seqFib(n);
//					current.setResult(new Result() {
//						
//						@Override
//						public Object result() {
//							return result;
//						}
//					});
//				}
//			}
//			
//			@Override
//			public String toString() {
//				return "Fib("+n+")";
//			}
//		};
		return new  AeminiumFibonacci.FibBody(n, rt);
	}

//	public static void main(String[] args) {
//		Runtime rt = Factory.getRuntime();
//		rt.init();
//
//		Task t1 = rt.createNonBlockingTask(new  AeminiumFibonacci.FibBody(MAX_CALC, rt), Runtime.NO_HINTS);
//		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
//		rt.shutdown();
//	}
}
