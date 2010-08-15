package aeminium.runtime.implementations;

import aeminium.runtime.Runtime;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.scheduler.RuntimeScheduler;

public abstract class AbstractRuntime implements Runtime {
	public static Runtime runtime;
	public static RuntimeGraph graph;
	public static RuntimeScheduler scheduler;
	public static RuntimePrioritizer prioritizer;
}
