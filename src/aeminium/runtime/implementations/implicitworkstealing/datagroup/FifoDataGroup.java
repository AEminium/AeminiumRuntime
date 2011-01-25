package aeminium.runtime.implementations.implicitworkstealing.datagroup;

import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.DataGroup;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitAtomicTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public final class FifoDataGroup implements DataGroup {	
	private boolean locked = false;
	private List<ImplicitTask> waitQueue = new LinkedList<ImplicitTask>();
	protected ImplicitTask owner = null;
	protected boolean checkForDeadlocks = Configuration.getProperty(FifoDataGroup.class, "checkForDeadlocks", false);
	
	public final boolean trylock(ImplicitTask task) {
		
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
				return true;
			}
		}
	}

	public final void unlock(ImplicitWorkStealingRuntime rt) {
		ImplicitTask head = null;
		synchronized (this) {
			locked = false;
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
			return "DataGroup[UNLOCKED]";
		} else {
			return "DataGroup[LOCKED"+owner+"]";
		}
	}

}