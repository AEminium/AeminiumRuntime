package aeminium.runtime.implementations.implicitworkstealing.decider;

import java.util.Arrays;
import java.util.HashMap;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public class ATC implements ParallelizationDecider {
	ImplicitWorkStealingRuntime rt;
	
	protected final int maxTotalTasksPerCoreThreshold  = Configuration.getProperty(getClass(), "maxTotalTasksPerCoreThreshold", 2);
	protected final int maxLevel  = Configuration.getProperty(getClass(), "maxLevelThreshold", 16);
	protected final static HashMap<String, Integer> cache = new HashMap<String, Integer>(); // function_level => ms
	
	@Override
	public void setRuntime(Runtime rt) {
		this.rt = (ImplicitWorkStealingRuntime) rt;
	}

	@Override
	public boolean parallelize(ImplicitTask current) {
		final String key = current.getClass().getName() + "_" + current.level; 
		if (cache.containsKey(key)) {
			return cache.get(key) > 1; // ms
		} else {
			
			// Save time
			final long start = System.nanoTime();
			Task saveTime = rt.createNonBlockingTask(new Body() {
				@Override
				public void execute(Runtime rt, Task current) throws Exception {
					int value = (int) ((System.nanoTime() - start) / 1000);
					cache.put(key, value);
				}
				
			}, Hints.SMALL);
			rt.schedule(saveTime, Runtime.NO_PARENT, Arrays.asList((Task)current));
			
			
			// Base Decision
			int totalTasks = rt.scheduler.getSubmissionQueueSize();
			for (WorkStealingThread thread: rt.scheduler.getThreads()) {
				totalTasks += thread.getTaskQueue().size(); 
			}
			return (totalTasks < rt.scheduler.getMaxParallelism() * maxTotalTasksPerCoreThreshold) &&
				(current.level < maxLevel);
		}
	}

}
