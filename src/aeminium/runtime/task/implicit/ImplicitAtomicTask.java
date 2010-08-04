package aeminium.runtime.task.implicit;

import java.util.Collection;
import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.task.RuntimeAtomicTask;

public class ImplicitAtomicTask<T extends ImplicitTask<T>> extends ImplicitTask<T> implements RuntimeAtomicTask<T> {
	protected RuntimeDataGroup<T> datagroup;
	
	public ImplicitAtomicTask(Body body, RuntimeDataGroup<T> datagroup,	Collection<Hints> hints, EnumSet<Flags> flags) {
		super(body, hints, flags);
		this.datagroup = datagroup;
	}

	@Override
	public Object call() throws Exception {
		@SuppressWarnings("unchecked")
		T Tthis = (T)this;
		if ( datagroup.trylock(Tthis) ) {
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
	public RuntimeDataGroup<T> getDataGroup() {
		synchronized (this) {
			return datagroup;
		}
	}

}