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

package aeminium.runtime.tools.benchmark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class FixedParallelMaxDependencies implements Benchmark {
	private static final String name = FixedParallelMaxDependencies.class.getSimpleName();
	private final int[] COUNTS = {100, 1000, 10000, 100000};
	private static final int taskCount = 16;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run(Reporter reporter) {
		for (int COUNT : COUNTS) {
			reporter.flush();
			runTest(reporter, COUNT);
		}
	}

	private void runTest(Reporter reporter, int count) {
		Runtime rt = Factory.getRuntime();
		rt.init();

		long start = System.nanoTime();
		List<Task> firstTasks = createTasks(rt, taskCount, "Task-0");
		List<Task> previousTasks = firstTasks;
		scheduleTasks(rt, previousTasks, Runtime.NO_DEPS);


		for(int i = 1; i < count; i++ ) {
			List<Task> nextTasks = createTasks(rt, taskCount, "Task-"+i);
			scheduleTasks(rt, nextTasks, previousTasks);
			previousTasks = nextTasks;
		}

		rt.shutdown();
		long end = System.nanoTime();

		String result = String.format("Run %10d tasks in %12d ns ==> %10d ns per task | %6d tasks/second.", (count*(long)taskCount), (end-start), ((end-start)/(count*(long)taskCount)),  (1000000000/((end-start)/(count*(long)taskCount))));
		reporter.reportLn(result);

	}

	private void scheduleTasks(Runtime rt, List<Task> tasks, Collection<Task> deps) {
		for ( Task t : tasks ) {
			rt.schedule(t, Runtime.NO_PARENT, deps);
		}
	}

	private List<Task> createTasks(Runtime rt, int count, final String name) {
		List<Task> tasks = new ArrayList<Task>(count);

		for (int i = 0; i < count; i++) {
			tasks.add(createTask(rt, name+"["+i+"]"));
		}

		return tasks;
	}

	private Task createTask(Runtime rt, final String name) {
		return rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task parent) {
				// DO NOTHING
			}

			@Override
			public String toString() {
				return name;
			}

		}, Runtime.NO_HINTS);
	}
}
