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

package aeminium.runtime.tests;

import java.util.Arrays;

import org.junit.Test;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class LinearDependencies extends BaseTest {
	@Test
	public void linearDependenciesTest() {
		Runtime rt = getRuntime();
		rt.init();

		Task t1 = rt.createNonBlockingTask(new Body() {
			@Override
			public void execute(Runtime rt, Task current) {
				// wait some time to allow other task to be inserted
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			@Override
			public String toString() {
				return "t1";
			}
		}, Runtime.NO_HINTS);

		Task t2 = rt.createNonBlockingTask(new Body() {
			@Override
			public void execute(Runtime rt, Task current) {
			}
			@Override
			public String toString() {
				return "t2";
			}
		}, Runtime.NO_HINTS);

		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(t2, Runtime.NO_PARENT, Arrays.asList(t1));

		rt.shutdown();
	}
}
