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

import org.junit.Test;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class TestChildTasks extends BaseTest {
	@Test
	public void childTasks() {
		Runtime rt = Factory.getRuntime();
		rt.init();

		Task t1 = createTask(rt, 2);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);

		rt.shutdown();
	}

	public Task createTask(final Runtime rt, final int level ) {
		return rt.createNonBlockingTask((Runtime rt1, Task current) -> {
                    if (level > 0) {
                        rt1.schedule(createTask(rt1, level-1), current, Runtime.NO_DEPS);
                    }
                }, Runtime.NO_HINTS);
	}
}
