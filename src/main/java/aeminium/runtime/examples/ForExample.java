package aeminium.runtime.examples;


import java.util.ArrayList;

import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.helpers.loops.ForBody;
import aeminium.runtime.helpers.loops.ForTask;
import aeminium.runtime.helpers.loops.Range;
import aeminium.runtime.implementations.Factory;

public class ForExample {

	public static void main(String[] args) {

		ArrayList<String> stringz = new ArrayList<String>();
		for (int i=0; i<64; i++) {
			stringz.add("Hello " + i);
		}

		sequentialVersion(stringz);
		System.out.println("--------");
		aeminiumVersion(stringz);

		System.out.println("-----------------------");

		sequentialRangeVersion();
		System.out.println("--------");
		aeminiumRangeVersion();
	}

	private static void sequentialVersion(ArrayList<String> stringz) {
		for (String s : stringz) {
			System.out.println(s.toUpperCase());
		}

	}

	private static void aeminiumVersion(ArrayList<String> stringz) {
		Runtime rt = Factory.getRuntime();

		rt.init();

		Task t = ForTask.createFor(rt, stringz, new ForBody<String>() {

			@Override
			public void iterate(String o, Runtime rt, Task current) {
				System.out.println(o.toUpperCase());
			}

		}, Runtime.NO_HINTS);

		rt.schedule(t, Runtime.NO_PARENT, Runtime.NO_DEPS);


		rt.shutdown();
	}

	private static void sequentialRangeVersion() {
		for (int i=0; i<100; i++) {
			System.out.println("Bye " + i);
		}

	}

	private static void aeminiumRangeVersion() {
		Runtime rt = Factory.getRuntime();

		rt.init();

		Task t = ForTask.createFor(rt, new Range(0, 100, 1), new ForBody<Integer>() {

			@Override
			public void iterate(Integer i, Runtime rt, Task current) {
				System.out.println("Bye " + i);
			}

		}, Runtime.NO_HINTS);

		rt.schedule(t, Runtime.NO_PARENT, Runtime.NO_DEPS);


		rt.shutdown();
	}
}
