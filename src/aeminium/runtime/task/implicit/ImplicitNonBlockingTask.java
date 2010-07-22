package aeminium.runtime.task.implicit;

import java.util.Collection;
import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.task.RuntimeNonBlockingTask;

public class ImplicitNonBlockingTask extends ImplicitTask implements RuntimeNonBlockingTask {

	public ImplicitNonBlockingTask(RuntimeGraph<ImplicitTask> graph, Body body, Collection<Hints> hints, EnumSet<Flags> flags) {
		super(graph, body, hints, flags);
	}
}
