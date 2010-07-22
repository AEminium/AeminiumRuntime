package aeminium.runtime.task.generic;

import java.util.Collection;
import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.task.RuntimeNonBlockingTask;
import aeminium.runtime.task.RuntimeTask;

public class GenericNonBlockingTask extends GenericTask implements RuntimeNonBlockingTask {

	public GenericNonBlockingTask(RuntimeGraph<GenericTask> graph, Body body, Collection<Hints> hints, EnumSet<Flags> flags) {
		super(graph, body, hints, flags);
		// TODO Auto-generated constructor stub
	}

}
