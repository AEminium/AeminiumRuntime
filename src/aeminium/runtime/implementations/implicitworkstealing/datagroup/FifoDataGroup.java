package aeminium.runtime.implementations.implicitworkstealing.datagroup;

import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.DataGroup;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public final class FifoDataGroup implements DataGroup {	
	private boolean locked = false;
	private List<ImplicitTask> waitQueue = new LinkedList<ImplicitTask>();
	private ImplicitTask owner = null;

	public final boolean trylock(ImplicitTask task) {
		synchronized (this) {
			if ( locked ) {
				waitQueue.add(task);
				return false;
			} else {
				locked = true;
				owner = task;
				return true;
			}
		}
	}

	public final void unlock(ImplicitWorkStealingRuntime rt) {
		synchronized (this) {
			locked = false;
			owner = null;
			if (!waitQueue.isEmpty()) {
				ImplicitTask head = waitQueue.remove(0);
				rt.scheduler.scheduleTask(head);
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