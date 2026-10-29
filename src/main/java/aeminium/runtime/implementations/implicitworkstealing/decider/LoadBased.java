package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public class LoadBased implements ParallelizationDecider {
	ImplicitWorkStealingRuntime rt;

	@Override
	public void setRuntime(Runtime rt) {
		this.rt = (ImplicitWorkStealingRuntime) rt;
	}

	@Override
	public boolean parallelize(ImplicitTask current) {
		for (WorkStealingThread thread: rt.scheduler.getThreads()) {
			if (thread.getLocalQueueSize() == 0)  return true;
		}
		return false;
	}

}
