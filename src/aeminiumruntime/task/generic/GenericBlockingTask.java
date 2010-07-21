package aeminiumruntime.task.generic;

import java.util.Collection;

import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.graph.RuntimeGraph;
import aeminiumruntime.task.RuntimeBlockingTask;
import aeminiumruntime.task.RuntimeTask;

public class GenericBlockingTask extends GenericTask implements RuntimeBlockingTask {

	public GenericBlockingTask(RuntimeGraph<RuntimeTask> graph, Body body,
			Collection<Hint> hints) {
		super(graph, body, hints);
	}

}
