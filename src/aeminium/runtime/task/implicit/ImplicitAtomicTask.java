package aeminium.runtime.task.implicit;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.task.RuntimeAtomicTask;

public final class ImplicitAtomicTask<T extends ImplicitTask<T>> extends ImplicitTask<T> implements RuntimeAtomicTask<T> {
	protected RuntimeDataGroup<T> datagroup;
	
	public ImplicitAtomicTask(Body body, RuntimeDataGroup<T> datagroup,	Collection<Hints> hints) {
		super(body, hints);
		this.datagroup = datagroup;
	}

	@Override
	public final Object call() throws Exception {
		@SuppressWarnings("unchecked")
		T Tthis = (T)this;
		if ( datagroup.trylock(Tthis) ) {
			super.call();
		}
		return null;		
	}

	@Override 
	public final void taskCompleted() {
		super.taskCompleted();
		datagroup.unlock();
	}

	@Override
	public final RuntimeDataGroup<T> getDataGroup() {
		synchronized (this) {
			return datagroup;
		}
	}

}