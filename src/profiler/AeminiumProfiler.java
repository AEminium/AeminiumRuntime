package profiler;

import java.util.LinkedList;

import aeminium.runtime.Profiler;
import aeminium.runtime.implementations.implicitworkstealing.graph.ImplicitGraph;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.BlockingWorkStealingScheduler;

public class AeminiumProfiler extends Thread implements Profiler {
	
	private final long SLEEP_PRECISION = 1;
	private final long SPIN_YIELD_PRECISION = 1;
	private final long SLEEPING_TIME = 1000;
	
	private BlockingWorkStealingScheduler scheduler;
	private ImplicitGraph graph;
	private LinkedList<DataCollection> dataList;
	
	private boolean isToContinue = true;
	
	public AeminiumProfiler(BlockingWorkStealingScheduler scheduler, ImplicitGraph graph) {
		
		this.scheduler = scheduler;
		this.graph = graph;
		this.dataList = new LinkedList<DataCollection>();
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
	        
	        System.out.println("OUT OUT OUT");
	    }
		
		System.out.println("We have collected: " + dataList.size());
	}
	
	@Override
	public synchronized void stopExecution()
	{
		System.out.println("HERE");
		this.isToContinue = false;
	}
}
