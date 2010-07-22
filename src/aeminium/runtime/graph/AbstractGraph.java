package aeminium.runtime.graph;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.RuntimeTask;

public abstract class AbstractGraph<T extends RuntimeTask> implements RuntimeGraph<T>{
	protected final RuntimePrioritizer<T> prioritizer;
	protected final EnumSet<Flags> flags;

	public AbstractGraph(RuntimePrioritizer<T> prioritizer, EnumSet<Flags> flags) {
		this.prioritizer = prioritizer;
		this.flags = flags;
	}
}
