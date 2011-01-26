package aeminium.runtime.implementations.implicitworkstealing.task;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;

public class ImplicitBlockingTask extends ImplicitTask implements BlockingTask {

	public ImplicitBlockingTask(Body body, short hints) {
		super(body, hints);
	}
}
