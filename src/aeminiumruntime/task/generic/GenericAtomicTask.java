package aeminiumruntime.task.generic;

import java.util.Collection;

import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.datagroup.RuntimeDataGroup;
import aeminiumruntime.graph.RuntimeGraph;
import aeminiumruntime.task.AbstractTask;
import aeminiumruntime.task.RuntimeAtomicTask;
import aeminiumruntime.task.RuntimeTask;

public class GenericAtomicTask<T extends RuntimeTask> extends AbstractTask implements
		RuntimeAtomicTask<T> {
	private final RuntimeDataGroup<T> datagroup;
	
	public GenericAtomicTask(RuntimeGraph<RuntimeTask> graph, Body body, RuntimeDataGroup<T> datagroup, Collection<Hint> hints) {
		super(graph, body, hints);
		this.datagroup = datagroup;
	}

	@Override
	public RuntimeDataGroup<T> getDataGroup() {
		return datagroup;
	}

}
