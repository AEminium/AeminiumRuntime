package aeminium.runtime.task.implicit;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.RuntimeTask;

public class ImplicitAtomicTask<T extends RuntimeTask> extends ImplicitTask implements RuntimeAtomicTask<T> {
	private final RuntimeDataGroup<T> datagroup;
	
	public ImplicitAtomicTask(RuntimeGraph<RuntimeTask> graph, Body body, RuntimeDataGroup<T> datagroup, Collection<Hints> hints) {
		super(graph, body, hints);
		this.datagroup = datagroup;
	}
	
	public RuntimeDataGroup<T> getDataGroup() {
		return datagroup;
	}

	@Override
	public Object call() throws Exception {
		boolean locked = datagroup.trylock((T)this);
		if ( locked ) {
			getBody().execute(this);
			graph.taskFinished(this);
		}
		return null;
	}

	@Override 
	public void taskCompleted() {
		datagroup.unlock();
	}

}