package aeminium.runtime.task.implicit;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.task.RuntimeBlockingTask;

public class ImplicitBlockingTask extends ImplicitTask implements RuntimeBlockingTask {

	public ImplicitBlockingTask(RuntimeGraph<ImplicitTask> graph, Body body, Collection<Hints> hints) {
		super(graph, body, hints);
	}

}
