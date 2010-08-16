package aeminium.runtime.implementations;

import aeminium.runtime.Runtime;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.scheduler.RuntimeScheduler;

@SuppressWarnings("unchecked")
public abstract class AbstractRuntime implements Runtime {
	public static Runtime runtime;
	public static RuntimeGraph graph;
	public static RuntimeScheduler scheduler;
	public static RuntimePrioritizer prioritizer;
	
	public static void setRuntime(Runtime runtime) {
		AbstractRuntime.runtime = runtime;
	}
	
	public static void setGraph(RuntimeGraph graph) {
		AbstractRuntime.graph = graph;
	}
	
	public static void setScheduler(RuntimeScheduler scheduler) {
		AbstractRuntime.scheduler = scheduler;
	}
	
	public static void setPrioritizer(RuntimePrioritizer prioritizer) {
		AbstractRuntime.prioritizer = prioritizer;
	}
	
	
}
