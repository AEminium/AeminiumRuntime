package aeminium.runtime.scheduler;

import java.util.Collection;

import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.taskcounter.RuntimeTaskCounter;

public interface RuntimeScheduler <T extends RuntimeTask> {	
	public void init(RuntimeTaskCounter tc);
	
	/**
	 * Schedules task in the order they have been passed in.
	 * 
	 * @param tasks
	 */
	public void scheduleTasks(Collection<T> tasks);
	
	public void scheduleTask(T task);
	
	/**
	 * Set the prioritizer to callback when tasks are finished.
	 * 
	 * @param prioritizer
	 */
	public void setPrioritizer(RuntimePrioritizer<T> prioritizer );
	
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
	
	/**
	 * Shutdown internal resources. It is not legal to scheduler more tasks after 
	 */
	public void shutdown();
}
