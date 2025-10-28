package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public class MaxTasks implements ParallelizationDecider {
	ImplicitWorkStealingRuntime rt;

	protected final int maxTotalTasksPerCoreThreshold  = Configuration.getProperty(getClass(), "maxTotalTasksPerCoreThreshold", 2);

	@Override
	public void setRuntime(Runtime rt) {
		this.rt = (ImplicitWorkStealingRuntime) rt;
	}

	@Override
	public boolean parallelize(ImplicitTask current) {
		int totalTasks = rt.scheduler.getSubmissionQueueSize();
		for (WorkStealingThread thread: rt.scheduler.getThreads()) {
			totalTasks += thread.getTaskQueue().size();
		}
		return (totalTasks < rt.scheduler.getMaxParallelism() * maxTotalTasksPerCoreThreshold);
	}

}
