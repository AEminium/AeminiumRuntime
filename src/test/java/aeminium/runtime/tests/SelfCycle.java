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
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import aeminium.runtime.Body;
import aeminium.runtime.ErrorHandler;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class SelfCycle extends BaseTest {
	@Test(timeout=2000)
	public void testSelfDeadlock() {
		final AtomicBoolean cycle = new AtomicBoolean(false);
		Runtime rt = getRuntime();
		rt.init();

		rt.addErrorHandler(new ErrorHandler() {

			@Override
			public void handleTaskException(Task task, Throwable t) {
				// TODO Auto-generated method stub

			}

			@Override
			public void handleTaskDuplicatedSchedule(Task task) {
				// TODO Auto-generated method stub

			}

			@Override
			public void handleLockingDeadlock() {
				// TODO Auto-generated method stub

			}

			@Override
			public void handleInternalError(Error err) {
				// TODO Auto-generated method stub

			}

			@Override
			public void handleDependencyCycle(Task task) {
				cycle.set(true);

			}
		});

		Task t1 = rt.createNonBlockingTask(createBody(1), Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Arrays.asList(t1));

		if ( !cycle.get() ) {
			Assert.fail("Did not detect self cylcle.");
			rt.shutdown();
		}

	}

	public Body createBody(final int i) {
		return new Body() {
			public void execute(Runtime rt, Task parent) {
				System.out.println("Task " + i);
			}

			public String toString() {
				return "" + i;
			}
		};
	}
}
