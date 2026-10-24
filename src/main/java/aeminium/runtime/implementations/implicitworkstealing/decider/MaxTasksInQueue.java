package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public class MaxTasksInQueue implements ParallelizationDecider {
	ImplicitWorkStealingRuntime rt;

	protected final int maxTasksInQueueThreshold  = Configuration.getProperty(getClass(), "maxTasksInQueueThreshold", 2);

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
		return (currentQueueSize < maxTasksInQueueThreshold);
	}

}
