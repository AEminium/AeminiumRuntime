package aeminium.runtime.task.implicit;

import aeminium.runtime.Body;
import aeminium.runtime.task.RuntimeNonBlockingTask;


public final class ImplicitNonBlockingTask<T extends ImplicitTask<T>> extends ImplicitTask<T> implements RuntimeNonBlockingTask {

	public ImplicitNonBlockingTask(Body body, short hints) {
		super(body, hints);
	}


}
