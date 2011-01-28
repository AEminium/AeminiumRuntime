package aeminium.runtime;

public interface ErrorHandler {
	/**
	 * Called when the execution of task's body threw exception 'e'.
	 * 
	 * @param task
	 * @param exception
	 */
	public void handleTaskException(final Task task, final Throwable t);
	
	/**
	 * Called when a deadlock in locking has been detected.
	 */
	public void handleLockingDeadlock();
	
	/**
	 * Called when a cycle in task's dependencies is detected.
	 * 
	 * @param task
	 */
	public void handleDependencyCycle(final Task task);
	
	/**
	 * Called if task has been scheduled twice.
	 * 
	 * @param task
	 */
	public void handleTaskDuplicatedSchedule(final Task task);
	
	/**
	 * Called when some internal error occurred.
	 * 
	 * @param err
	 */
	public void handleInternalError(final Error err);
}
