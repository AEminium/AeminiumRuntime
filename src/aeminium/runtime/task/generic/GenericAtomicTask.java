package aeminium.runtime.task.generic;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.RuntimeTask;

public class GenericAtomicTask<T extends RuntimeTask> extends AbstractTask implements
		RuntimeAtomicTask<T> {
	private final RuntimeDataGroup<T> datagroup;
	
	public GenericAtomicTask(RuntimeGraph<RuntimeTask> graph, Body body, RuntimeDataGroup<T> datagroup, Collection<Hints> hints) {
		super(graph, body, hints);
		this.datagroup = datagroup;
	}

	@Override
	public RuntimeDataGroup<T> getDataGroup() {
		return datagroup;
	}

}
