package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public class DefaultDecider implements ParallelizationDecider {

	protected final int parallelizeThreshold  = Configuration.getProperty(getClass(), "parallelizeThreshold", 3);
	protected ImplicitWorkStealingRuntime runtime = null;
	
	@Override
	public boolean parallelize(ImplicitTask current) {
		int c=0;
		for (WorkStealingThread thread: runtime.scheduler.getThreads()) {
			c += thread.getTaskQueue().size(); 
		}
		if (c < runtime.scheduler.getMaxParallelism() * parallelizeThreshold) return true;
		return false;
	}

	@Override
	public void setRuntime(Runtime rt) {
		this.runtime = (ImplicitWorkStealingRuntime) rt;
	}

}
