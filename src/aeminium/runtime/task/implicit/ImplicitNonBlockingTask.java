package aeminium.runtime.task.implicit;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.task.RuntimeNonBlockingTask;
import aeminium.runtime.task.RuntimeTask;

public class ImplicitNonBlockingTask extends ImplicitTask implements RuntimeNonBlockingTask {

	public ImplicitNonBlockingTask(RuntimeGraph<RuntimeTask> graph, Body body, Collection<Hints> hints) {
		super(graph, body, hints);
	}
}
