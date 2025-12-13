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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.DataGroup;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.error.ErrorManager;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitAtomicTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;
import aeminium.runtime.utils.graphviz.DiGraphViz;
import aeminium.runtime.utils.graphviz.GraphViz.Color;
import aeminium.runtime.utils.graphviz.GraphViz.LineStyle;


/*
 * A DataGroup (DG) implementation that works similar to a lock.
 */
public final class FifoDataGroup implements ImplicitWorkStealingRuntimeDataGroup {
	protected static final boolean checkForDeadlocks        = Configuration.getProperty(FifoDataGroup.class, "checkForDeadlocks", false);
	protected static final boolean graphVizEnabled          = Configuration.getProperty(ImplicitWorkStealingRuntime.class, "enableGraphViz", false);
	protected static final boolean graphVizShowLockingOrder = Configuration.getProperty(FifoDataGroup.class, "graphVizShowLockingOrder", false);
	protected static AtomicInteger idGen = new AtomicInteger();
	private boolean locked = false;
	private List<ImplicitTask> waitQueue = new LinkedList<ImplicitTask>();
	protected ImplicitTask owner = null;
	protected ImplicitTask previousOwner;
	protected final int id = idGen.incrementAndGet();

	// Tries to hold the lock on the DG.
	public final boolean trylock(ImplicitWorkStealingRuntime rt, ImplicitTask task) {

		synchronized (this) {
			if ( locked ) {
				waitQueue.add(task);
				if ( checkForDeadlocks ) {
					ImplicitAtomicTask atomicParent = ((ImplicitAtomicTask)task).getAtomicParent();
					atomicParent.addDataGroupDependecy(this);
					checkForDeadlock(atomicParent, rt.getErrorManager());
				}
				return false;
			} else {
				locked = true;
				owner = task;
				if ( graphVizEnabled && graphVizShowLockingOrder && previousOwner != null ) {
					DiGraphViz graphViz = rt.getGraphViz();
					graphViz.addConnection(owner.hashCode(), previousOwner.hashCode(), LineStyle.DOTTED, Color.GREEN, ""+id);
				}
				return true;
			}
		}
	}

	// Releases the lock on the DG
	public final void unlock(ImplicitWorkStealingRuntime rt, ImplicitTask task) {
		ImplicitTask head = null;
		synchronized (this) {
			locked = false;
			previousOwner = owner;
			owner = null;
			if (!waitQueue.isEmpty()) {
				head = waitQueue.remove(0);
				if ( checkForDeadlocks ) {
					ImplicitAtomicTask atomicParent = ((ImplicitAtomicTask)head).getAtomicParent();
					atomicParent.addDataGroupDependecy(this);
				}
			}
		}
		if ( head != null ) {
			rt.scheduler.scheduleTask(head);
		}
	}

	public final boolean checkForDeadlock(final ImplicitAtomicTask atomicParent,
					                      final ErrorManager em) {
		for ( DataGroup dg : atomicParent.getDataGroupDependencies() ) {
			if ( checkForDeadlock(atomicParent, (ImplicitAtomicTask)((FifoDataGroup)dg).owner, em) ) {
				return true;
			}
		}
		return false;
	}

	public final boolean checkForDeadlock(final ImplicitAtomicTask atomicParent,
			                              final ImplicitAtomicTask current,
			                              final ErrorManager em) {
		boolean result = false;
		if ( atomicParent == current ) {
			em.signalLockingDeadlock();
			result = true;
		} else {
			for ( DataGroup dg : current.getDataGroupDependencies() ) {
				if ( checkForDeadlock(atomicParent, ((ImplicitAtomicTask)((FifoDataGroup)dg).owner).getAtomicParent(), em) ) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public final String toString() {
		if ( locked == false ) {
			return "DataGroup["+id+"|UNLOCKED]";
		} else {
			return "DataGroup["+id+"|LOCKED"+owner+"]";
		}
	}

}
