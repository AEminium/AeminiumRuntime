package profiler;

import java.util.LinkedList;

import aeminium.runtime.Profiler;
import aeminium.runtime.implementations.implicitworkstealing.graph.ImplicitGraph;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.BlockingWorkStealingScheduler;

public class AeminiumProfiler extends Thread implements Profiler {
	
	private final long SLEEP_PRECISION = 1;
	private final long SPIN_YIELD_PRECISION = 1;
	private final long SLEEPING_TIME = 1;
	
	private BlockingWorkStealingScheduler scheduler;
	private ImplicitGraph graph;
	private LinkedList<DataCollection> dataList;
	
	private volatile boolean isToContinue = true;
	
	public AeminiumProfiler(BlockingWorkStealingScheduler scheduler, ImplicitGraph graph) {
		
		this.scheduler = scheduler;
		this.graph = graph;
		this.dataList = new LinkedList<DataCollection>();
		
		this.start();
	}
	
	public void run() {

		while (isToContinue) {
			DataCollection data = new DataCollection();
			
			/* We first collect the data concerning the graph. */
			
			/* Then, the data collected with the scheduler. */
			
			
			dataList.add(data);
			
	        final long end = System.nanoTime() + SLEEPING_TIME;
	        long timeLeft = SLEEPING_TIME;
	
	        /* Sleeps for the defined time before collecting new data. */
	        do {
	            if (timeLeft > SLEEP_PRECISION)
					try {
						Thread.sleep (SLEEPING_TIME);
					} catch (Exception e) {
						System.out.println("ERROR ON PROFILER: " + e.getMessage());
						return;
					}
				else
	                if (timeLeft > SPIN_YIELD_PRECISION)
	                    Thread.yield();
	
	            timeLeft = end - System.nanoTime();
	
	        } while (timeLeft > 0 && isToContinue);
	    }
	}
	
	@Override
	public void stopExecution() {
		
		System.out.println("Profiler has been ordered to stop...");
		this.isToContinue = false;
	}
	
	@Override
	public LinkedList<DataCollection> getDataList() {
		return dataList;
	}
}
