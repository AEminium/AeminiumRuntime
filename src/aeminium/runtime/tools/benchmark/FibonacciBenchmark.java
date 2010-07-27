package aeminium.runtime.tools.benchmark;

import java.util.Arrays;
import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.task.implicit.ImplicitTask;
import aeminium.runtime.task.implicit.ImplicitTaskState;



public class FibonacciBenchmark implements Benchmark {
	private final String name = "FibonacciBenchmark";
	private int[] input = {4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 ,26};
	public final static ResultOne RESULT_ONE = new FibonacciBenchmark().new ResultOne();
	
	abstract class Result {
		public abstract Object result();
		
		public String toString() {
			return "Result";
		}
	}

	final class  ResultOne extends Result {
		private final Integer one = new Integer(1);
				
		@Override
		public Object result() {
			return one;
		}
		
	}
	
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

		final Task root = fib(rt, n);
		rt.schedule(root, Runtime.NO_PARENT, Runtime.NO_DEPS);

		rt.shutdown();	

		long end = System.nanoTime();
		
		String result = String.format("Fib(%3d) =  %7d in %12d ns", n, ((Result)root.getResult()).result(), (end-start));
		reporter.reportLn(result);
		reporter.flush();

	}

	public Task fib(final Runtime rt, final int n) {
		return rt.createNonBlockingTask(new Body() {
			
			@Override
			public void execute(final Task current) {
				//System.out.println("n="+n);
				if ( 2 < n ) {
					final Task f1  = fib(rt, n-1);
					rt.schedule(f1, current, Runtime.NO_DEPS);
					
					final Task f2  = fib(rt, n-2);
					rt.schedule(f2, current, Runtime.NO_DEPS);
					
					current.setResult(new Result() {
						@Override
						public Object result() {
							Integer v1 = (Integer)((Result)f1.getResult()).result();
							Integer v2 = (Integer)((Result)f1.getResult()).result();
							return v1 + v2;
						}
					});
				} else {
					current.setResult(RESULT_ONE);
				}
			}
			
			@Override
			public String toString() {
				return "Fib("+n+")";
			}
		}, Runtime.NO_HINTS);
	}
}
