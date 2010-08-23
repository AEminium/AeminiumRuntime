package aeminium.runtime.scheduler;

import aeminium.runtime.implementations.implicitworkstealing.events.RuntimeEventManager;
import aeminium.runtime.task.RuntimeTask;

public interface RuntimeScheduler <T extends RuntimeTask> {	
	public void init(RuntimeEventManager eventManager);
	
	/**
	 * Shutdown internal resources. It is not legal to scheduler more tasks after 
	 */
	public void shutdown();
	
	public void scheduleTask(T task);

	/**
	 * Return the number of task that can be executed in parallel.
	 * @return
	 */
	public int getMaxParallelism();
	
	/**
	 * Returns the number of running tasks. 
	 *
	 * @return
	 */
	public int getRunningTasks();
	
	/**
	 * Returns the number of paused tasks 
	 * 
	 * @return
	 */
	public int getPausedTasks();
	
	/**
	 * Callback when a task has finished its execution.
	 * 
	 * @param task
	 */
	public void taskFinished(T task);
	
	/**
	 * Callback when task is paused (e.g. while waiting for 
	 * datagroup to become available).
	 * 
	 * @param task
	 */
	public void taskPaused(T task);
	
	/**
	 * Callback when task is supposed to be resumed, meaning to 
	 * be re-scheduled by the scheduler.
	 * 
	 * @param task
	 */
	public void taskResume(T task);

}
