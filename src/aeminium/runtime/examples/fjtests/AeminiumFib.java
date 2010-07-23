

package aeminium.runtime.examples.fjtests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class AeminiumFib {

	private static int MAX_CALC = 47;
	private static int THRESHOLD = 13;

	public static Body createFibBody(final Runtime rt, final int n) {
		return new Body() {
			
			public int seqFib(int n) {
				if (n <= 2) return 1;
				else return (seqFib(n-1) + seqFib(n-2));
			}
			
			public void execute(final Task currentTask) {
				if (n <= THRESHOLD) {
					currentTask.setResult(seqFib(n));
				} else {

					final Task branch1 = rt.createNonBlockingTask(createFibBody(rt,
							n - 2), Runtime.NO_HINTS);
					rt.schedule(branch1, currentTask, Runtime.NO_DEPS);

					final Task branch2 = rt.createNonBlockingTask(createFibBody(rt,
							n - 1), Runtime.NO_HINTS);
					rt.schedule(branch2, currentTask, Runtime.NO_DEPS);

					Task join = rt.createNonBlockingTask(new Body() {
						public void execute(Task p) {
							int r1 = (Integer) branch1.getResult();
							int r2 = (Integer) branch2.getResult();
							currentTask.setResult(r1 + r2);
							// System.out.println("Merging " + n + " -> " + currentTask.getResult());
						}
						
						public String toString() {
							return "Merge for n="+n;
						}
					}, Runtime.NO_HINTS);
					rt.schedule(join, currentTask,  Arrays.asList(branch1, branch2));
				}

			}
			
			@Override
			public String toString() {
				return "Recursive for n=" + n;
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
						System.out.println("Final result:" + calc.getResult());
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
