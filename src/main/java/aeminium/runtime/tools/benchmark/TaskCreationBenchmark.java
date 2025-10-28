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


public class TaskCreationBenchmark implements Benchmark {
	private static final String name = "TaskCreation";
	private final int[] COUNTS = { 100, 1000, 10000, 100000, 1000000};

	@Override
	public void run(Reporter reporter) {
		Body body = new Body() {
			@Override
			public void execute(Runtime rt, Task parent) {
				// DO NOTHING
			}
		};

		Runtime rt = Factory.getRuntime();
		rt.init();

		for ( int COUNT : COUNTS) {
			long start = System.nanoTime();
			for (int i = 0; i < COUNT; i++) {
				@SuppressWarnings("unused")
				Task t = rt.createNonBlockingTask(body, Runtime.NO_HINTS);
			}
			long end = System.nanoTime();

			String result = String.format("Created %10d tasks in %12d ns ==> %10d ns per task creation.", COUNT, (end-start), ((end-start)/COUNT));
			reporter.reportLn(result);
		}
		rt.shutdown();
	}

	@Override
	public String getName() {
		return name;
	}
}
