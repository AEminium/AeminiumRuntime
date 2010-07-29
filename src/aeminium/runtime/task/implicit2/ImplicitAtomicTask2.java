package aeminium.runtime.task.implicit2;

import java.util.Collection;
import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeAtomicTask;

public class ImplicitAtomicTask2<T extends ImplicitTask2> extends ImplicitTask2<T> implements RuntimeAtomicTask {
	protected RuntimeDataGroup datagroup;
	
	public ImplicitAtomicTask2(RuntimeGraph<T> graph, Body body, RuntimeDataGroup datagroup,	Collection<Hints> hints, EnumSet<Flags> flags) {
		super(graph, body, hints, flags);
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