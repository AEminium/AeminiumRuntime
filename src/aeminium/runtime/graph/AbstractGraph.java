package aeminium.runtime.graph;

import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractGraph<T extends RuntimeTask> implements RuntimeGraph<T>{
	protected final RuntimePrioritizer<T> prioritizer;

	public AbstractGraph(RuntimePrioritizer<T> prioritizer) {
		this.prioritizer = prioritizer;
	}
}
