package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public class TestDecider implements ParallelizationDecider {

	protected final int parallelizeThreshold  = Configuration.getProperty(getClass(), "parallelizeThreshold", 3);
	
	@Override
	public boolean parallelize(ImplicitTask current) {
		Thread thread = Thread.currentThread();
		if ( thread instanceof WorkStealingThread ) {
			if ( ((WorkStealingThread)thread).getTaskQueue().size() > parallelizeThreshold ) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}

	@Override
	public void setRuntime(Runtime rt) {
		// This class ignores rt.
	}

}
