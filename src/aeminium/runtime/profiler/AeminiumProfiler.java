package aeminium.runtime.profiler;

import java.util.LinkedList;

import aeminium.runtime.Profiler;
import aeminium.runtime.implementations.implicitworkstealing.graph.ImplicitGraph;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.AeminiumThread;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.BlockingWorkStealingScheduler;

public class AeminiumProfiler extends AeminiumThread implements Profiler {
	
	private final long SLEEP_PRECISION = 100;
	private final long SPIN_YIELD_PRECISION = 10;
	private final long SLEEPING_TIME = 1;
	
	private BlockingWorkStealingScheduler scheduler;
	private ImplicitGraph graph;
	private LinkedList<DataCollection> dataList;
	
	private volatile boolean shutdown = false;
	
	public AeminiumProfiler(BlockingWorkStealingScheduler scheduler, ImplicitGraph graph) {
		
		this.scheduler = scheduler;
		this.graph = graph;
		this.dataList = new LinkedList<DataCollection>();
		
		this.start();
	}
	
	@Override
	public void run() {

		while (!shutdown) {
			DataCollection data = new DataCollection(scheduler.getMaxParallelism());
			
			/* We first collect the data concerning the graph. */
			graph.collectData(data);
			/* Then, the data collected with the scheduler. */
			scheduler.collectData(data);
			
			dataList.add(data);
			
	        final long end = System.nanoTime() + SLEEPING_TIME;
	        long timeLeft = SLEEPING_TIME;
	
	        /* Sleeps for the defined time before collecting new data. */
	        do {
	            if (timeLeft > SLEEP_PRECISION)
					try {
						Thread.sleep (1);
					} catch (Exception e) {
						System.out.println("ERROR ON PROFILER: " + e.getMessage());
						return;
					}
				else
	                if (timeLeft > SPIN_YIELD_PRECISION)
	                    Thread.yield();
	
	            timeLeft = end - System.nanoTime();
	
	        } while (timeLeft > 0 && shutdown);
	    }
	}
	
	@Override
	public void shutdown() {
		System.out.println("Profiler has been ordered to stop...");
		this.shutdown = true;
	}
	
	@Override
	public LinkedList<DataCollection> getDataList() {
		return dataList;
	}
}
