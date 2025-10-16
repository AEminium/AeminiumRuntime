package aeminium.runtime.profiler;

import aeminium.runtime.implementations.implicitworkstealing.graph.ImplicitGraph;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.BlockingWorkStealingScheduler;

/* This class must be used so we can statically access the scheduler and the graph
 * from the class CountersProbe.
 */
public class AeminiumProfiler {

	public static BlockingWorkStealingScheduler scheduler;
	public static ImplicitGraph graph;

	public AeminiumProfiler(BlockingWorkStealingScheduler scheduler, ImplicitGraph graph) {

		AeminiumProfiler.scheduler = scheduler;
		AeminiumProfiler.graph = graph;
	}
}
