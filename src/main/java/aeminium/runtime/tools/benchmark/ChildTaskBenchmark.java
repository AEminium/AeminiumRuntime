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

public class ChildTaskBenchmark implements Benchmark {
	private static final String name = "ChildTaskBenchmark";
	private int[] levels = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	private int fanout = 4;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run(Reporter reporter) {
		for (int level : levels) {
			runTest(reporter, level);
			reporter.flush();
		}
	}

	public void runTest(Reporter reporter, int level) {
		Runtime rt = Factory.getRuntime();
			long start = System.nanoTime();
			rt.init();

			Task root = creatTaskWithChildren(rt, level, level, fanout);
			rt.schedule(root, Runtime.NO_PARENT, Runtime.NO_DEPS);

			rt.shutdown();
			long end = System.nanoTime();

			String result = String.format("Level %3d in %12d ns.", level, (end-start));
			reporter.reportLn(result);

	}

	public Task creatTaskWithChildren(final Runtime rt, final int level, final int MAX_LEVEL, final int fanout) {
		return rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task current) {
//				StringBuilder sb = new StringBuilder();
//				for ( int i =  0; i < (MAX_LEVEL - level) ; i++) {
//					sb.append(" ");
//				}
//				sb.append("Task@level"+level);
//				System.out.println(sb.toString());

				if ( 0 < level ) {
					for ( int i = 0; i < fanout; i++ ) {
						Task childTask = creatTaskWithChildren(rt, level-1, MAX_LEVEL, fanout);
						rt.schedule(childTask, current, Runtime.NO_DEPS);
					}
				}
			}
		}, Runtime.NO_HINTS);
	}
}
