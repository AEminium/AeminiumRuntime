package aeminium.runtime.implementations.implicitworkstealing.decider;

import java.util.concurrent.ConcurrentHashMap;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public class ATC implements ParallelizationDecider {
	ImplicitWorkStealingRuntime rt;

	protected final int maxTotalTasksPerCoreThreshold  = Configuration.getProperty(getClass(), "maxTotalTasksPerCoreThreshold", 2);
	protected final int maxLevel  = Configuration.getProperty(getClass(), "maxLevelThreshold", 16);
	protected final static ConcurrentHashMap<String, Integer> cache = new ConcurrentHashMap<String, Integer>(); // function_level => ms

	@Override
	public void setRuntime(Runtime rt) {
		this.rt = (ImplicitWorkStealingRuntime) rt;
	}

	@Override
	public boolean parallelize(ImplicitTask current) {
		if (current != null) {
			final String key = current + "_" + current.level;
			if (cache.containsKey(key)) {
				return cache.get(key) > 1; // ms
			} else {
				// Save time
				final long start = System.nanoTime();

				current.setFinishedCallback(new Runnable() {
					@Override
					public void run() {
						int value = (int) ((System.nanoTime() - start) / 1000000);
						cache.put(key, value);
					}
				});
			}
		}
		// Base Decision
		int totalTasks = rt.scheduler.getSubmissionQueueSize();
		for (WorkStealingThread thread: rt.scheduler.getThreads()) {
			totalTasks += thread.getTaskQueue().size();
		}
		int level = current == null ? 1 : current.level;

		return (totalTasks < rt.scheduler.getMaxParallelism() * maxTotalTasksPerCoreThreshold) &&
			(level < maxLevel);
	}

}
