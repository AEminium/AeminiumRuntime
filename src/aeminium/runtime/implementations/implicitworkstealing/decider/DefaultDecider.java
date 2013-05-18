package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;

public class DefaultDecider implements ParallelizationDecider {

	protected final int parallelizeThreshold  = Configuration.getProperty(getClass(), "parallelizeThreshold", 3);
	protected ImplicitWorkStealingRuntime runtime = null;
	
	@Override
	public boolean parallelize() {
		for (WorkStealingThread thread: runtime.scheduler.getThreads()) {
			if (thread.getTaskQueue().size() < parallelizeThreshold) return true;
		}
		return false;
	}

	@Override
	public void setRuntime(Runtime rt) {
		this.runtime = (ImplicitWorkStealingRuntime) rt;
	}

}
