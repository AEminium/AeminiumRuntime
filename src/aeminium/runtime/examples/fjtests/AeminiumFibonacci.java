package aeminium.runtime.examples.fjtests;

import java.util.ArrayList;
import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

abstract class Result {
	public abstract Object result();
	
	public String toString() {
		return "Result";
	}
}

public class AeminiumFibonacci {

	private static final int MAX_CALC = 46;
	private static final int THRESHOLD = 23;

	public static Body createFibBody(final Runtime rt, final int n) {
		return new Body() {
			
			public int seqFib(int n) {
				if (n <= 2) return 1;
				else return (seqFib(n-1) + seqFib(n-2));
			}
			
			@Override
			public void execute(final Task current) {
				//System.out.println("n="+n);
				if ( THRESHOLD < n ) {
					final Task f1  = rt.createNonBlockingTask(createFibBody(rt, n-1), Runtime.NO_HINTS);
					rt.schedule(f1, current, Runtime.NO_DEPS);
					
					final Task f2  = rt.createNonBlockingTask(createFibBody(rt, n-2), Runtime.NO_HINTS);
					rt.schedule(f2, current, Runtime.NO_DEPS);
					
					current.setResult(new Result() {
						@Override
						public Object result() {
							Integer v1 = (Integer)((Result)f1.getResult()).result();
							Integer v2 = (Integer)((Result)f2.getResult()).result();
							return v1 + v2;
						}
					});
				} else {
					final Integer result = seqFib(n);
					current.setResult(new Result() {
						
						@Override
						public Object result() {
							return result;
						}
					});
				}
			}
			
			@Override
			public String toString() {
				return "Fib("+n+")";
			}
		};
	}

	public static void main(String[] args) {
		final Runtime rt = Factory.getRuntime();
		rt.init();

		Task t1 = rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Task p) {
				final Task calc = rt.createNonBlockingTask(createFibBody(rt, MAX_CALC), Runtime.NO_HINTS);
				rt.schedule(calc, p, Runtime.NO_DEPS);

				Collection<Task> printDeps = new ArrayList<Task>();
				printDeps.add(calc);

				Task print = rt.createBlockingTask(new Body() {
					@Override
					public void execute(Task p) {
						System.out.println("Final result:" + ((Result) calc.getResult()).result());
					}
					
					@Override
					public String toString() {
						return "Final result";
					}
				}, Runtime.NO_HINTS);
				rt.schedule(print, p, printDeps);
			}

			@Override
			public String toString() {
				return "Master Task";
			}
			
		}, Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.shutdown();
	}
}
