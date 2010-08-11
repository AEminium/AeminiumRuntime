package aeminium.runtime.task.implicit;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.task.RuntimeNonBlockingTask;


public final class ImplicitNonBlockingTask<T extends ImplicitTask<T>> extends ImplicitTask<T> implements RuntimeNonBlockingTask {

	public ImplicitNonBlockingTask(Body body, Collection<Hints> hints) {
		super(body, hints);
	}

}
