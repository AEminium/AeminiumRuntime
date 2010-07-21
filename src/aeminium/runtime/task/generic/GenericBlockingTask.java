package aeminium.runtime.task.generic;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.task.RuntimeBlockingTask;
import aeminium.runtime.task.RuntimeTask;

public class GenericBlockingTask extends GenericTask implements RuntimeBlockingTask {

	public GenericBlockingTask(RuntimeGraph<RuntimeTask> graph, Body body,
			Collection<Hints> hints) {
		super(graph, body, hints);
	}

}
