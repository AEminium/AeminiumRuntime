package aeminium.runtime.implementations.implicitworkstealing.task;

import aeminium.runtime.Body;
import aeminium.runtime.NonBlockingTask;


public final class ImplicitNonBlockingTask extends ImplicitTask implements NonBlockingTask {

	public ImplicitNonBlockingTask(Body body, short hints) {
		super(body, hints);
	}


}
