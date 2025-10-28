package aeminium.runtime.profiler;

public class DataCollection {

	/* Scheduler variables. */
	public int noOccupiedQueues;
	public int [] taskInNonBlockingQueue;
	public int taskInBlockingQueue;
	public int [][] tasksHandled;
	/* Graph variables. */
	/* This vector discriminates between the three different types
	 * of tasks, while the variable noCompletedTasks is sum of them
	 * all.
	 */
	public int noTasksCompleted[];

	public int noUnscheduledTasks;
	public int noWaitingForChildrenTasks;
	public int noWaitingForDependenciesTasks;
	public int noTasksWaitingInQueue;
	public int noRunningTasks;
	public int noCompletedTasks;

	public final static int ATOMIC_TASK = 0;
	public final static int NON_BLOCKING_TASK = 1;
	public final static int BLOCKING_TASK = 2;

	public DataCollection (int maxParallelism) {
		/* Scheduler variables. */
		taskInNonBlockingQueue = new int[maxParallelism];
		tasksHandled = new int[maxParallelism][3];
		taskInBlockingQueue = 0;
		/* Graph variables. */
		noTasksCompleted = new int[3];

	}


}
