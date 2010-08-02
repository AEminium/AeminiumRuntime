package aeminium.runtime.task.implicit;

import java.util.Collection;
import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeAtomicTask;

public class ImplicitAtomicTask<T extends ImplicitTask> extends ImplicitTask<T> implements RuntimeAtomicTask {
	protected RuntimeDataGroup datagroup;
	
	public ImplicitAtomicTask(Body body, RuntimeDataGroup datagroup,	Collection<Hints> hints, EnumSet<Flags> flags) {
		super(body, hints, flags);
		this.datagroup = datagroup;
	}

	@Override
	public Object call() throws Exception {
		if ( datagroup.trylock(this) ) {
			super.call();
		}
		return null;		
	}

	@Override 
	public void taskCompleted() {
		super.taskCompleted();
		datagroup.unlock();
	}

	@Override
	public RuntimeDataGroup getDataGroup() {
		synchronized (this) {
			return datagroup;
		}
	}

}