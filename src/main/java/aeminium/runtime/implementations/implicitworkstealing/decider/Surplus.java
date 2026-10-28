package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public class Surplus implements ParallelizationDecider {
	ImplicitWorkStealingRuntime rt;

	protected final int surplusThreshold  = Configuration.getProperty(getClass(), "surplusThreshold", 3);

	@Override
	public void setRuntime(Runtime rt) {
		this.rt = (ImplicitWorkStealingRuntime) rt;
	}

	@Override
	public boolean parallelize(ImplicitTask current) {
		int currentQueueSize;
		Thread t = Thread.currentThread();

		if (t instanceof WorkStealingThread) {
			WorkStealingThread wst = (WorkStealingThread) t;
			currentQueueSize = wst.getLocalQueueSize();
		} else {
			currentQueueSize = rt.scheduler.getSubmissionQueueSize();
		}

		int idle = rt.scheduler.getIdleThreadCount();
		int active = rt.scheduler.getActiveThreadCount();
		int otherQueues = currentQueueSize * idle/active;

		return (currentQueueSize - otherQueues < surplusThreshold);
	}

}
