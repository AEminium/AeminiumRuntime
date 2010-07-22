package aeminium.runtime.task.generic;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.RuntimeTask;

public class GenericAtomicTask extends AbstractTask implements 	RuntimeAtomicTask<GenericTask> {
	private final RuntimeDataGroup<GenericTask> datagroup;
	
	public GenericAtomicTask(RuntimeGraph<GenericTask> graph, Body body, RuntimeDataGroup<GenericTask> datagroup, Collection<Hints> hints) {
		super(graph, body, hints);
		this.datagroup = datagroup;
	}

	@Override
	public RuntimeDataGroup<GenericTask> getDataGroup() {
		return datagroup;
	}

}
