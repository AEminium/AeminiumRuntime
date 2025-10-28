/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 *
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

package aeminium.runtime.examples;

import java.util.ArrayList;
import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class SimpleTestOriginal {
	private static int MAX_CALC = 30;

	public static void main(String[] args) {
        final Runtime rt = Factory.getRuntime();
		rt.init();

		Body b1 = new Body() {
			public void execute(Runtime rt, Task parent) {
				int sum = 0;
				for (int i = 0; i < MAX_CALC; i++) {
					sum += i;
				}
				System.out.println("Sum: " + sum);
			}
		};

		Body b2 = new Body() {
			public void execute(Runtime rt, Task parent) {
				for (int i = 0; i < MAX_CALC / 5; i++) {
					System.out.println("Processing...");
				}
			}
		};

		Body b3 = new Body() {
			public void execute(Runtime rt, Task parent) {
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
			public void execute(Runtime rt, Task parent) {
				Tests.power(2, 20);
			}
		};

		Body b5 = new Body() {
			public void execute(Runtime rt, Task parent) {
				Tests.matrixMultiplication();
			}
		};

		Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
		Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
		Task t3 = rt.createNonBlockingTask(b3, Runtime.NO_HINTS);
		Task t4 = rt.createNonBlockingTask(b4, Runtime.NO_HINTS);
		Task t5 = rt.createNonBlockingTask(b5, Runtime.NO_HINTS);

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
