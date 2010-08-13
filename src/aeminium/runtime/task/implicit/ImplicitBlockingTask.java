package aeminium.runtime.task.implicit;

import aeminium.runtime.Body;
import aeminium.runtime.task.RuntimeBlockingTask;

public final class ImplicitBlockingTask<T extends ImplicitTask<T>> extends ImplicitTask<T> implements 	RuntimeBlockingTask {

	public ImplicitBlockingTask(Body body, long hints) {
		super(body, hints);
	}
}
