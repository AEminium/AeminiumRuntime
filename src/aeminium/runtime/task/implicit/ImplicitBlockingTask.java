package aeminium.runtime.task.implicit;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.task.RuntimeBlockingTask;

public final class ImplicitBlockingTask<T extends ImplicitTask<T>> extends ImplicitTask<T> implements 	RuntimeBlockingTask {

	public ImplicitBlockingTask(Body body, Collection<Hints> hints) {
		super(body, hints);
	}
}
