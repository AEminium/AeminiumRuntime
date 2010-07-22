package aeminium.runtime.task.generic;

import java.util.Collection;
import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.RuntimeAtomicTask;

public class GenericAtomicTask extends AbstractTask implements 	RuntimeAtomicTask<GenericTask> {
	private final RuntimeDataGroup<GenericTask> datagroup;
	
	public GenericAtomicTask(RuntimeGraph<GenericTask> graph, Body body, RuntimeDataGroup<GenericTask> datagroup, Collection<Hints> hints, EnumSet<Flags> flags) {
		super(graph, body, hints, flags);
		this.datagroup = datagroup;
	}

	@Override
	public RuntimeDataGroup<GenericTask> getDataGroup() {
		return datagroup;
	}

}
