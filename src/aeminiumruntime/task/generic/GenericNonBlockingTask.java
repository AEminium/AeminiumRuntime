package aeminiumruntime.task.generic;

import java.util.Collection;

import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.graph.RuntimeGraph;
import aeminiumruntime.task.RuntimeNonBlockingTask;
import aeminiumruntime.task.RuntimeTask;

public class GenericNonBlockingTask extends GenericTask implements
		RuntimeNonBlockingTask {

	public GenericNonBlockingTask(RuntimeGraph<RuntimeTask> graph, Body body,
			Collection<Hint> hints) {
		super(graph, body, hints);
		// TODO Auto-generated constructor stub
	}

}
