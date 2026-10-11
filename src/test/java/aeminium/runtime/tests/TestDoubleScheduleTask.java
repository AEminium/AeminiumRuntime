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

import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import aeminium.runtime.ErrorHandler;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class TestDoubleScheduleTask extends BaseTest {

	@Test()
	public void scheduleTaskTwice() {
		final AtomicBoolean twice = new AtomicBoolean(false);
		Runtime rt = Factory.getRuntime();
		rt.init();
		
		rt.addErrorHandler(new ErrorHandler() {
			
			@Override
			public void handleTaskException(Task task, Throwable t) {
				
			}
			
			@Override
			public void handleTaskDuplicatedSchedule(Task task) {
				twice.set(true);
				
			}
			
			@Override
			public void handleLockingDeadlock() {
				
			}
			
			@Override
			public void handleInternalError(Error err) {
				
			}
			
			@Override
			public void handleDependencyCycle(Task task) {
				
			}
		});
		
		Task t = rt.createNonBlockingTask((Runtime rt1, Task current) -> {}, Runtime.NO_HINTS);

		rt.schedule(t, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.schedule(t, Runtime.NO_PARENT, Runtime.NO_DEPS);

		if ( !twice.get() ) {
			System.out.println("not twice");
			fail("Did not detect doubly scheduled task");
		}
		
		rt.shutdown();
	}
}
