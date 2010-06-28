package aeminiumruntime.examples.fjtests;

import java.util.ArrayList;
import java.util.Collection;

import aeminiumruntime.Runtime;
import aeminiumruntime.Task;
import aeminiumruntime.Body;
import aeminiumruntime.launcher.RuntimeFactory;

public class AeminiumFib {

	private static int MAX_CALC = 47;
    final static Runtime rt = RuntimeFactory.getDebugRuntime();

	public static Body createFibBody(final int n, final int[] solution,
			final int solpos) {
		return new Body() {
			public void execute(Task p) {
				if (n <= 1) {
					Task base = rt.createNonBlockingTask(new Body() {
						public void execute(Task p) {
							solution[solpos] = 1;
						}
					});
					rt.schedule(base, Runtime.NO_PARENT, Runtime.NO_DEPS);
				} else {
					final int[] previous = { -1, -1 };
					Collection<Task> branchesDeps = new ArrayList<Task>();

					Task branch1 = rt.createNonBlockingTask(createFibBody(
							n - 2, previous, 0));
					rt.schedule(branch1, Runtime.NO_PARENT, Runtime.NO_DEPS);
					branchesDeps.add(branch1);

					Task branch2 = rt.createNonBlockingTask(createFibBody(
							n - 1, previous, 1));
					rt.schedule(branch2, Runtime.NO_PARENT, Runtime.NO_DEPS);
					branchesDeps.add(branch2);

					Task join = rt.createNonBlockingTask(new Body() {
						public void execute(Task p) {
							solution[solpos] = previous[0] + previous[1];
							System.out.println(solution[solpos]);
						}
					});
					rt.schedule(join, Runtime.NO_PARENT, branchesDeps);
				}

			}
		};
	}

	public static void main(String[] args) {
		rt.init();

		Task t1 = rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Task p) {
				final int[] result = { -1 };
				Task calc = rt.createNonBlockingTask(createFibBody(MAX_CALC,
						result, 0));
				rt.schedule(calc, Runtime.NO_PARENT, Runtime.NO_DEPS);

				Collection<Task> printDeps = new ArrayList<Task>();
				printDeps.add(calc);

				Task print = rt.createBlockingTask(new Body() {
					@Override
					public void execute(Task p) {
						System.out.println(result[0]);
					}
				});
				rt.schedule(print, Runtime.NO_PARENT, printDeps);
			}
		});
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.shutdown();
	}
}
