package aeminium.runtime.task.generic;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.task.RuntimeNonBlockingTask;
import aeminium.runtime.task.RuntimeTask;

public class GenericNonBlockingTask extends GenericTask implements RuntimeNonBlockingTask {

	public GenericNonBlockingTask(RuntimeGraph<GenericTask> graph, Body body, Collection<Hints> hints) {
		super(graph, body, hints);
		// TODO Auto-generated constructor stub
	}

}
