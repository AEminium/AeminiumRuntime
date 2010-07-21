package aeminiumruntime.task.implicit;

import java.util.Collection;

import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.graph.RuntimeGraph;
import aeminiumruntime.task.RuntimeNonBlockingTask;
import aeminiumruntime.task.RuntimeTask;

public class ImplicitNonBlockingTask extends ImplicitTask implements RuntimeNonBlockingTask {

	public ImplicitNonBlockingTask(RuntimeGraph<RuntimeTask> graph, Body body, Collection<Hint> hints) {
		super(graph, body, hints);
	}
}
