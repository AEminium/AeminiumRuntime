package aeminiumruntime.task.implicit;

import java.util.Collection;

import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.datagroup.RuntimeDataGroup;
import aeminiumruntime.graph.RuntimeGraph;
import aeminiumruntime.task.RuntimeAtomicTask;
import aeminiumruntime.task.RuntimeTask;

public class ImplicitAtomicTask<T extends RuntimeTask> extends ImplicitTask implements RuntimeAtomicTask<T> {
	private final RuntimeDataGroup<T> datagroup;
	
	public ImplicitAtomicTask(RuntimeGraph<RuntimeTask> graph, Body body, RuntimeDataGroup<T> datagroup, Collection<Hint> hints) {
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