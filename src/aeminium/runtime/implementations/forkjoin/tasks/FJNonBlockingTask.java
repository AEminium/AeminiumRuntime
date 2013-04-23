package aeminium.runtime.implementations.forkjoin.tasks;

import aeminium.runtime.Body;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.implementations.forkjoin.FJRuntime;

@SuppressWarnings("serial")
public class FJNonBlockingTask extends FJTask implements NonBlockingTask {

	public FJNonBlockingTask(Body b, short hints, FJRuntime rt) {
		super(b, hints, rt);
	}

}
