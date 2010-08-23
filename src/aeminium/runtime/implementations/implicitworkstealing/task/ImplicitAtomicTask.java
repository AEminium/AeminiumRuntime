package aeminium.runtime.implementations.implicitworkstealing.task;

import aeminium.runtime.AtomicTask;
import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.datagroup.FifoDataGroup;

public final class ImplicitAtomicTask extends ImplicitTask implements AtomicTask {
	protected FifoDataGroup datagroup;
	
	public ImplicitAtomicTask(Body body, FifoDataGroup datagroup,	short hints) {
		super(body, hints);
		this.datagroup = datagroup;
	}

	@Override
	public final void invoke(ImplicitWorkStealingRuntime rt) {
		if ( datagroup.trylock(this) ) {
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