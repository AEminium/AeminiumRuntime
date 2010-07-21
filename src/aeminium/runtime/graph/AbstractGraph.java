package aeminium.runtime.graph;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flag;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractGraph<T extends RuntimeTask> implements RuntimeGraph<T>{
	protected final RuntimePrioritizer<T> prioritizer;

	public AbstractGraph(EnumSet<Flag> flags, RuntimePrioritizer<T> prioritizer) {
		this.prioritizer = prioritizer;
	}
}
