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

package aeminium.runtime.implementations.implicitworkstealing.datagroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitAtomicTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

/*
 * A DataGroup (DG) implementation that supports nested atomic tasks on the same DG.
 * It works by having a stack of locks that grows as nesting increases.
 */
public final class NestedAtomicTasksDataGroup implements ImplicitWorkStealingRuntimeDataGroup {
	protected final ArrayList<ImplicitWorkStealingRuntimeDataGroup> dataGroups = new ArrayList<ImplicitWorkStealingRuntimeDataGroup>();
	protected final ImplicitWorkStealingRuntimeDataGroupFactory factory;
	protected static final AtomicInteger idGen = new AtomicInteger();
	protected final int id = idGen.incrementAndGet();

	public NestedAtomicTasksDataGroup(ImplicitWorkStealingRuntimeDataGroupFactory factory) {
		this.factory = factory;
	}

	@Override
	public final synchronized boolean trylock(ImplicitWorkStealingRuntime rt, ImplicitTask task) {
		return getLock(task).trylock(rt, task);
	}

	@Override
	public final synchronized void unlock(ImplicitWorkStealingRuntime rt, ImplicitTask task) {
		getLock(task).unlock(rt, task);
	}

	protected ImplicitWorkStealingRuntimeDataGroup getLock(ImplicitTask task) {
		final int level = getLockLevel(task);
		dataGroups.ensureCapacity(level+1);
		if ( dataGroups.size() <= level ) {
			dataGroups.add(level, factory.create());
		}
		return dataGroups.get(level);
	}

	protected int getLockLevel(ImplicitTask task) {
		if ( task == null ) {
			return -1; // remove the starting task itself
		}
		if ( task instanceof ImplicitAtomicTask ) {
			ImplicitAtomicTask atomicTask = (ImplicitAtomicTask)task;
			if ( atomicTask.getDataGroup() == this ) {
				return 1 + getLockLevel(atomicTask.parent);
			} else {
				return getLockLevel(atomicTask.parent);
			}
		} else {
			return getLockLevel(task.parent);
		}
	}

	public interface ImplicitWorkStealingRuntimeDataGroupFactory {
		public ImplicitWorkStealingRuntimeDataGroup create();
	}

	@Override
	public String toString() {
		return "HierarchicalDataGroup["+id+"]" + Arrays.deepToString(dataGroups.toArray());
	}
}
