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

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class BlockingTaskTests extends BaseTest {

	@Test
	public void singleTask() {
		Runtime rt = getRuntime();
		rt.init();

		Task task = rt.createBlockingTask(new Body() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				getLogger().info("Single Task");
			}
		}, Runtime.NO_HINTS);
		rt.schedule(task, Runtime.NO_PARENT, Runtime.NO_DEPS);

		rt.shutdown();
	}


	@Test
	public void manyTasks() {
		Runtime rt = getRuntime();
		rt.init();

		final int TASK_COUNT = 200;
		final AtomicInteger counter = new AtomicInteger();

		for( int i = 0 ; i < TASK_COUNT ; i++ ) {
			Task task = rt.createBlockingTask(new Body() {
				@Override
				public void execute(Runtime rt, Task current) throws Exception {
					counter.incrementAndGet();
				}
			}, Runtime.NO_HINTS);
			rt.schedule(task, Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		rt.shutdown();
		assertTrue( counter.get() == TASK_COUNT );
	}

	@Test
	public void manyTasksMix() {
		Runtime rt = getRuntime();
		rt.init();

		final int TASK_COUNT = 200;
		final AtomicInteger counter = new AtomicInteger();

		for( int i = 0 ; i < TASK_COUNT ; i++ ) {
			Task taskB = rt.createBlockingTask(new Body() {
				@Override
				public void execute(Runtime rt, Task current) throws Exception {
					counter.incrementAndGet();
				}
			}, Runtime.NO_HINTS);
			rt.schedule(taskB, Runtime.NO_PARENT, Runtime.NO_DEPS);

			Task taskN = rt.createNonBlockingTask(new Body() {
				@Override
				public void execute(Runtime rt, Task current) throws Exception {
					counter.incrementAndGet();
				}
			}, Runtime.NO_HINTS);
			rt.schedule(taskN, Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		rt.shutdown();
		assertTrue( counter.get() == 2*TASK_COUNT );
	}
}
