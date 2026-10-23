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

package aeminium.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;


/*
 * Aeminium Runtime
 *
 * Provides an interface to create new tasks and submit them for execution.
 * Aeminium Programs should call init() when starting and should
 * call shutdown() at the end.
 */
public interface Runtime {
    /* Global constants for default values */
    public final static Collection<Task> NO_DEPS = new ArrayList<Task>() {
		private static final long serialVersionUID = 1852797887380877437L;

		@Override
		public String toString() {
			return "NO_DEPS";
		}
	};
    public final static short NO_HINTS = Hints.NO_HINTS;
    public final static Task NO_PARENT = new Task() {
		@Override
		public void setResult(Object value) {
			throw new Error("Cannot set result on NO_PARENT");
		}

		@Override
		public Object getResult() {
			throw new Error("Cannot get result from NO_PARENT");
		}

		@Override
		public String toString() {
			return "NO_PARENT";
		}
	};

    /* Initializes runtime. Should be called upon program start. */
    public void init() ;
    /*
     * Waits until all tasks are executed and frees
     * all allocated resources.
     * Should be called at the end of the program.
     */
    public void shutdown();
    /* Waits until all tasks have been processed */
    public void waitToEmpty();
    /*
     * Submits a task for execution, providing the parent task as well
     * as its dependencies.
     *  */
    public void schedule(Task task, Task parent, Collection<Task> deps) ;

    /* Checks whether runtime has enough tasks to process. */
    public boolean parallelize(Task t);

    /* Creates a new data group object */
    public DataGroup createDataGroup() ;
    /* Creates a new Blocking task */
    public BlockingTask createBlockingTask(Body b, short hints) ;
    /* Creates a new NonBlocking task */
    public NonBlockingTask createNonBlockingTask(Body b, short hints) ;
    /* Creates a new Atomic task */
    public AtomicTask createAtomicTask(Body b, DataGroup g, short hints) ;

    /* Return executor service abstraction for this runtime object */
    public ExecutorService getExecutorService();

    /* Add/Remove error handlers */
    public void addErrorHandler(ErrorHandler eh);
    public void removeErrorHandler(ErrorHandler eh);

    /* Returns an estimation of how many tasks were created. */
	public int getTaskCount();
}
