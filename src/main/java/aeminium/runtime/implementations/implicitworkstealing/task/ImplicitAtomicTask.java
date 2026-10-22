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

package aeminium.runtime.implementations.implicitworkstealing.task;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import aeminium.runtime.AtomicTask;
import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.datagroup.ImplicitWorkStealingRuntimeDataGroup;

/*
 * Handles all synchronization and locking related to the DataGroups associated with an
 * Atomic Task.
 * */
public final class ImplicitAtomicTask extends ImplicitTask implements AtomicTask {
	protected ImplicitWorkStealingRuntimeDataGroup datagroup;
	protected ImplicitAtomicTask atomicParent = null;
	protected volatile Set<DataGroup> requiredGroups;

	public ImplicitAtomicTask(Body body, ImplicitWorkStealingRuntimeDataGroup datagroup, short hints, boolean enableProfiler) {
		super(body, hints, enableProfiler);
		this.datagroup = datagroup;
	}

	/* Associates another datagroup with the current Task. */
	public void addDataGroupDependecy(DataGroup required) {
		if ( requiredGroups == null ) {
			synchronized (this) {
				if (requiredGroups == null ) {
					requiredGroups = Collections.synchronizedSet(new HashSet<DataGroup>());
				}
			}
		}
		requiredGroups.add(required);
	}

	/* Detaches another datagroup with the current Task. */
	public void removeDataGroupDependecy(DataGroup required) {
		if ( requiredGroups == null ) {
			synchronized (this) {
				if (requiredGroups == null ) {
					requiredGroups = Collections.synchronizedSet(new HashSet<DataGroup>());
				}
			}
		}
		requiredGroups.remove(required);
	}

	/* Retrives all datagroups associated with the current task. */
	public Set<DataGroup> getDataGroupDependencies() {
		if ( requiredGroups == null ) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(requiredGroups);
		}
	}

	/* In case of nested synchronization, returns the task that held the lock
	 * before executing this task.
	 *  */
	public  ImplicitAtomicTask getAtomicParent() {
		ImplicitAtomicTask result = atomicParent;
		if ( result == null ) {
			synchronized (this) {
				atomicParent = this;
				// search upwards
				ImplicitTask parent = this.parent;
				while ( parent != null ) {
					if ( parent instanceof ImplicitAtomicTask) {
						atomicParent = (ImplicitAtomicTask)parent;
					}
					parent = parent.parent;
				}
				result = atomicParent;
			}
		}
		return result;
	}

	@Override
	public final void invoke(ImplicitWorkStealingRuntime rt) {
		if ( datagroup.trylock(rt, this) ) {
			super.invoke(rt);
		}
	}

	@Override
	public final void taskCompleted(ImplicitWorkStealingRuntime rt) {
		datagroup.unlock(rt, this);
		super.taskCompleted(rt);
	}

	public final DataGroup getDataGroup() {
		synchronized (this) {
			return datagroup;
		}
	}

}
