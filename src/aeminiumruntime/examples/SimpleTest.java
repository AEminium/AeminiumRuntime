package aeminiumruntime.examples;

import java.util.ArrayList;
import java.util.Collection;

import aeminiumruntime.Runtime;
import aeminiumruntime.Task;
import aeminiumruntime.Body;
import aeminiumruntime.simpleparallel.ParallelRuntime;

public class SimpleTest {
	private static int MAX_CALC = 30;

	public static void main(String[] args) {
		final Runtime rt = new ParallelRuntime();
		rt.init();

		Body b1 = new Body() {
			public void execute(Task parent) {
				int sum = 0;
				for (int i = 0; i < MAX_CALC; i++) {
					sum += i;
				}
				System.out.println("Sum: " + sum);
			}
		};

		Body b2 = new Body() {
			public void execute(Task parent) {
				for (int i = 0; i < MAX_CALC / 5; i++) {
					System.out.println("Processing...");
				}
			}
		};

		Body b3 = new Body() {
			public void execute(Task parent) {
				int max = 0;
				for (int i = 0; i < MAX_CALC; i++) {
					if (i > max)
						max = i;
					System.out.println("Calculating Maximum...");

				}
				System.out.println("Maximum: " + max);
			}
		};

		Body b4 = new Body() {
			public void execute(Task parent) {
				Tests.power(2, 20);
			}
		};

		Body b5 = new Body() {
			public void execute(Task parent) {
				Tests.matrixMultiplication();
			}
		};

		Task t1 = rt.createNonBlockingTask(b1);
		Task t2 = rt.createNonBlockingTask(b2);
		Task t3 = rt.createNonBlockingTask(b3);
		Task t4 = rt.createNonBlockingTask(b4);
		Task t5 = rt.createNonBlockingTask(b5);

		// ex: deps2 == task2 dependencies
		Collection<Task> deps2 = new ArrayList<Task>();
		Collection<Task> deps4 = new ArrayList<Task>();
		Collection<Task> deps5 = new ArrayList<Task>();

		deps2.add(t1);
		deps4.add(t1);
		deps4.add(t3);
		deps5.add(t2);
		deps5.add(t4);

		rt.schedule(t3, Runtime.NO_PARENT, Runtime.NO_DEPS); // both null and
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(t5, Runtime.NO_PARENT, deps5);
		rt.schedule(t4, Runtime.NO_PARENT, deps4);
		rt.schedule(t2, Runtime.NO_PARENT, deps2);
		rt.shutdown();
	}
}
