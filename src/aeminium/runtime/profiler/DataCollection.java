package aeminium.runtime.profiler;

public class DataCollection {
	
	private final int NO_BLOCKING_QUEUES = 1;
	//TODO: Add all the variables that we are looking for.
	public int noOccupiedQueues;
	public int [] taskInNonBlockingQueue;
	public int [] taskInBlockingQueue;
	public int [] tasksHandled;
	
	public DataCollection (int maxParallelism) {
		
		taskInNonBlockingQueue = new int[maxParallelism];
		tasksHandled = new int[maxParallelism];
		taskInBlockingQueue = new int[NO_BLOCKING_QUEUES];
		
	}
	

}
