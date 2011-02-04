package aeminium.runtime.implementations.implicitworkstealing.datagroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitAtomicTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

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
