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

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.ErrorHandler;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class AtomicTaskDeadLock  extends BaseTest {

	@Test(timeout=2000)
	public void createAtomicTaskDeadLock() {
		final AtomicBoolean deadlock = new AtomicBoolean(false);
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
				deadlock.set(true);
			}

			@Override
			public void handleInternalError(Error err) {
				// TODO Auto-generated method stub

			}

			@Override
			public void handleDependencyCycle(Task task) {
				// TODO Auto-generated method stub

			}
		});

		DataGroup dg1 = rt.createDataGroup();
		DataGroup dg2 = rt.createDataGroup();

		Task t1 = createAtomicTask(rt, dg1, dg2);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t2 = createAtomicTask(rt, dg2, dg1);
		rt.schedule(t2, Runtime.NO_PARENT, Runtime.NO_DEPS);

		try {
			Thread.sleep(1500);
		} catch (InterruptedException e1) {}

		if ( !deadlock.get() ) {
			Assert.fail("Could not find deadlock");
			rt.shutdown();
		}
	}

	private Task createAtomicTask(final Runtime rt, final DataGroup dg1, final DataGroup dg2) {
		return rt.createAtomicTask(new Body() {

			@Override
			public void execute(Runtime rt, Task current) {
				getLogger().info("Atomic Task for data group : " + dg1);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rt.schedule(createAtomicTask(rt, dg2), current, Runtime.NO_DEPS);
			}
		}, dg1, Runtime.NO_HINTS);
	}

	private Task createAtomicTask(final Runtime rt, final DataGroup dg) {
		return rt.createAtomicTask(new Body() {

			@Override
			public void execute(Runtime rt, Task current) {
				getLogger().info("Atomic Sub-Task for data group : " + dg);
			}
		},  dg, Runtime.NO_HINTS);
	}
}
