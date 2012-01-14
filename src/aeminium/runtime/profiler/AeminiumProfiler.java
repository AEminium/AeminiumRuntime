package aeminium.runtime.profiler;

import aeminium.runtime.implementations.implicitworkstealing.graph.ImplicitGraph;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.BlockingWorkStealingScheduler;

public class AeminiumProfiler {
	
	public static BlockingWorkStealingScheduler scheduler;
	public static ImplicitGraph graph;

	public AeminiumProfiler(BlockingWorkStealingScheduler scheduler, ImplicitGraph graph) {
		
		AeminiumProfiler.scheduler = scheduler;
		AeminiumProfiler.graph = graph;
	}
}
