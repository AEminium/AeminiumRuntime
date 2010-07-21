package aeminium.runtime.scheduler;

import aeminium.runtime.task.RuntimeTask;

public interface RuntimeScheduler <T extends RuntimeTask> {	
	/**
	 * Schedules task in the order they have been passed in.
	 * 
	 * @param tasks
	 */
	public void scheduleTasks(T ... tasks);
	
	/**
	 * Shutdown internal resources. It is not legal to scheduler more tasks after 
	 */
	public void shutdown();
}
