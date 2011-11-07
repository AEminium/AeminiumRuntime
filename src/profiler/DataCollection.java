package profiler;

public class DataCollection {
	
	private final int NO_NON_BLOCKING_QUEUES = 4;
	private final int NO_BLOCKING_QUEUES = 4;
	//TODO: Add all the variables that we are looking for.
	int noOccupiedQueues;
	int [] taskInNonBlockingQueue;
	int [] taskInBlockingQueue;
	
	public DataCollection () {
		
		taskInNonBlockingQueue = new int[NO_NON_BLOCKING_QUEUES];
		taskInBlockingQueue = new int[NO_BLOCKING_QUEUES];
		
	}
	

}
