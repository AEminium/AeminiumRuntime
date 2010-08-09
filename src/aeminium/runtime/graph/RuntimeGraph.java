package aeminium.runtime.graph;

import java.util.Collection;

import aeminium.runtime.Task;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskDescription;

public interface RuntimeGraph <T extends RuntimeTask> {

	public void init();
	
	public void shutdown();
	
	/**
	 * Add a new task to the graph.
	 * @param task
	 */
	public abstract void addTask(T task, Task parent, Collection<T> deps);
	
	/**
	 * Callback function for task that has complteted.
	 * 
	 * @param task
	 */
	public abstract void taskCompleted(T task);
	
	
	/**
	 * Method to wait until all task have completed.
	 */
	public abstract void waitToEmpty();
	
	public abstract TaskDescription<T> getTaskDescription(T task);
}
