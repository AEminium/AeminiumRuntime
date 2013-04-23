package aeminium.runtime.implementations.forkjoin.tasks;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.implementations.forkjoin.FJRuntime;

@SuppressWarnings("serial")
public class FJBlockingTask extends FJTask implements BlockingTask {

	public FJBlockingTask(Body b, short hints, FJRuntime rt) {
		super(b, hints, rt);
	}
	
}
