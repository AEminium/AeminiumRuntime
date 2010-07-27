package aeminium.runtime.task.implicit2;

import java.util.Collection;
import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.task.RuntimeBlockingTask;

public class ImplicitBlockingTask2<T extends ImplicitTask2> extends ImplicitTask2<T> implements 	RuntimeBlockingTask {

	public ImplicitBlockingTask2(RuntimeGraph<T> graph, Body body,
			Collection<Hints> hints, EnumSet<Flags> flags) {
		super(graph, body, hints, flags);
	}
}
