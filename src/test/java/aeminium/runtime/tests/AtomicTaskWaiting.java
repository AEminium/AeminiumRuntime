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
import aeminium.runtime.DataGroup;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class AtomicTaskWaiting extends BaseTest {

	@Test
	public void runAtomicTaskWaitingTest() {
		Runtime rt = getRuntime();
		rt.init();

		DataGroup dg = rt.createDataGroup();
		Task t1 = createAtomicTask(rt, dg, 110);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t2 = createAtomicTask(rt, dg, 120);
		rt.schedule(t2, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t3 = createAtomicTask(rt, dg, 130);
		rt.schedule(t3, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t4 = createAtomicTask(rt, dg, 140);
		rt.schedule(t4, Runtime.NO_PARENT, Runtime.NO_DEPS);
		Task t5 = createAtomicTask(rt, dg, 150);
		rt.schedule(t5, Runtime.NO_PARENT, Runtime.NO_DEPS);

		rt.shutdown();
	}

	public Task createAtomicTask(final Runtime rt, DataGroup group, final int delay) {
		return rt.createAtomicTask(new Body() {
			@Override
			public void execute(Runtime rt, Task current) {
				// let's create some sub tasks
				Task t1 = rt.createNonBlockingTask(new Body() {
					@Override
					public void execute(Runtime rt, Task current) {
						getLogger().info("Sub Task waiting for "+ (delay+1) + " ms");
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
					@Override
					public String toString() {
						return ""+(delay+1);
					}
				}, Runtime.NO_HINTS);
				rt.schedule(t1, current, Runtime.NO_DEPS);

				Task t2 = rt.createNonBlockingTask(new Body() {
					@Override
					public void execute(Runtime rt, Task current) {
						getLogger().info("Sub Task waiting for "+ (delay+2) + " ms");
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
					@Override
					public String toString() {
						return ""+(delay+2);
					}

				}, Runtime.NO_HINTS);
				rt.schedule(t2, current, Arrays.asList(t1));

				getLogger().info("Task waiting for "+delay + " ms");
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			@Override
			public String toString() {
				return ""+delay;
			}
		} , group, Runtime.NO_HINTS);
	}
}
