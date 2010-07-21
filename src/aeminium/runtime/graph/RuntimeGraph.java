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
	 * Callback function for task that have finished. Finished 
	 * means that the body of the corresponding graph has 
	 * finished its execution. Note that there might still be 
	 * some sub-task pending.
	 * 
	 * @param task
	 */
	public abstract void taskFinished(T task);
	
	/**
	 * Method to wait until all task have completed.
	 */
	public abstract void waitToEmpty();
	
	public abstract TaskDescription<T> getTaskDescription(T task);
}
