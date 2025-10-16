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

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class IndependentTaskGraph implements Benchmark {
	private static final String name = "IndepenetTaskGraph";
	private final int[] COUNTS = {100, 1000, 10000, 100000, 1000000};

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run(Reporter reporter) {
		for (int COUNT : COUNTS) {
			runTest(reporter, COUNT);
			reporter.flush();
		}
	}

	private void runTest(Reporter reporter, int count) {
		Runtime rt = Factory.getRuntime();

		rt.init();

		long start = System.nanoTime();
		for(int i = 0; i < count; i++ ) {
			Task nextTask = createTask(rt);
			rt.schedule(nextTask, Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		rt.shutdown();
		long end = System.nanoTime();
		String result = String.format("Run %10d tasks in %12d ns ==> %10d ns per task | %6d tasks/second.", count, (end-start), ((end-start)/count), (1000000000/((end-start)/count)));
		reporter.reportLn(result);

	}

	private Task createTask(Runtime rt) {
		return rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task parent) {
				// DO NOTHING
			}
		}, Runtime.NO_HINTS);
	}
}
