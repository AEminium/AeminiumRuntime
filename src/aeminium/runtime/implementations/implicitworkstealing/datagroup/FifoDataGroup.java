package aeminium.runtime.implementations.implicitworkstealing.datagroup;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.DataGroup;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitAtomicTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;
import aeminium.runtime.utils.graphviz.DiGraphViz;
import aeminium.runtime.utils.graphviz.GraphViz.Color;
import aeminium.runtime.utils.graphviz.GraphViz.LineStyle;

public final class FifoDataGroup implements DataGroup {	
	protected static final boolean checkForDeadlocks        = Configuration.getProperty(FifoDataGroup.class, "checkForDeadlocks", false);
	protected static final boolean graphVizEnabled          = Configuration.getProperty(ImplicitWorkStealingRuntime.class, "enableGraphViz", false);
	protected static final boolean graphVizShowLockingOrder = Configuration.getProperty(FifoDataGroup.class, "graphVizShowLockingOrder", false);
	protected static AtomicInteger idGen = new AtomicInteger();
	private boolean locked = false;
	private List<ImplicitTask> waitQueue = new LinkedList<ImplicitTask>();
	protected ImplicitTask owner = null;
	protected ImplicitTask previousOwner;
	protected final int id = idGen.incrementAndGet();
	
	public final boolean trylock(ImplicitWorkStealingRuntime rt, ImplicitTask task) {
		
		synchronized (this) {
			if ( locked ) {
				waitQueue.add(task);
				if ( checkForDeadlocks ) {
					ImplicitAtomicTask atomicParent = ((ImplicitAtomicTask)task).getAtomicParent();
					atomicParent.addDataGroupDependecy(this);
					checkForDeadlock(atomicParent);
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

	public final void unlock(ImplicitWorkStealingRuntime rt) {
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

	public void checkForDeadlock(ImplicitAtomicTask atomicParent) {
		for ( DataGroup dg : atomicParent.getDataGroupDependencies() ) {
			checkForDeadlock(atomicParent, (ImplicitAtomicTask)((FifoDataGroup)dg).owner);
		}
	}
	
	public void checkForDeadlock(ImplicitAtomicTask atomicParent, ImplicitAtomicTask current) {
		if ( atomicParent == current ) {
			throw new RuntimeError("DeadLock");
		} else {			
			for ( DataGroup dg : current.getDataGroupDependencies() ) {
				checkForDeadlock(atomicParent, ((ImplicitAtomicTask)((FifoDataGroup)dg).owner).getAtomicParent());
			}
		}
	}
		
	public final String toString() {
		if ( locked == false ) {
			return "DataGroup["+id+"|UNLOCKED]";
		} else {
			return "DataGroup["+id+"|LOCKED"+owner+"]";
		}
	}

}