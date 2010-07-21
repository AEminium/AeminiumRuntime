package aeminiumruntime.task.implicit;

import java.util.Collection;

import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.graph.RuntimeGraph;
import aeminiumruntime.task.RuntimeBlockingTask;
import aeminiumruntime.task.RuntimeTask;

public class ImplicitBlockingTask extends ImplicitTask implements RuntimeBlockingTask {

	public ImplicitBlockingTask(RuntimeGraph<RuntimeTask> graph, Body body, Collection<Hint> hints) {
		super(graph, body, hints);
	}

}
