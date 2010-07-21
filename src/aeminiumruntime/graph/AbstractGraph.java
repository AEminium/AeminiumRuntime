package aeminiumruntime.graph;

import java.util.EnumSet;

import aeminiumruntime.implementations.Flag;
import aeminiumruntime.prioritizer.RuntimePrioritizer;
import aeminiumruntime.task.RuntimeTask;

public abstract class AbstractGraph<T extends RuntimeTask> implements RuntimeGraph<T>{
	protected final RuntimePrioritizer<T> prioritizer;

	public AbstractGraph(EnumSet<Flag> flags, RuntimePrioritizer<T> prioritizer) {
		this.prioritizer = prioritizer;
	}
}
