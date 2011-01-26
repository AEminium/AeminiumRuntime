package aeminium.runtime.implementations.implicitworkstealing.task;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import aeminium.runtime.AtomicTask;
import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.datagroup.FifoDataGroup;

public final class ImplicitAtomicTask extends ImplicitTask implements AtomicTask {
	protected FifoDataGroup datagroup;
	protected ImplicitAtomicTask atomicParent = null;
	protected Set<DataGroup> requiredGroups;

	
	public ImplicitAtomicTask(Body body, FifoDataGroup datagroup,	short hints) {
		super(body, hints);
		this.datagroup = datagroup;
	}

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
	
	public Set<DataGroup> getDataGroupDependencies() {
		if ( requiredGroups == null ) {
			return Collections.emptySet();
		} else {			
			return Collections.unmodifiableSet(requiredGroups);
		}
	}
	
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
		super.taskCompleted(rt);
		datagroup.unlock(rt);
	}

	public final DataGroup getDataGroup() {
		synchronized (this) {
			return datagroup;
		}
	}

}