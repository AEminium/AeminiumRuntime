package aeminium.runtime.profiler;

public class TaskInfo {

	public long creation = 0;
	public long enteredQueue = 0;
	public long exitedQueue = 0;
	public long startedExecution = 0;
	public long endedExecution = 0;

	public long addedToGraph = 0;

	/* Variables for each graph state a thread can
	 * possible go through.
	 */
	public long unscheduled = 0;
	public long waitingForDependencies = 0;
	public long waitingForChildren = 0;
	public long running = 0;
	public long completed = 0;

	public TaskInfo() {

		this.creation = System.nanoTime();
	}

}
