package aeminium.runtime.prioritizer;

import aeminium.runtime.task.RuntimeTask;

public interface RuntimePrioritizer<T extends RuntimeTask> {
	public void scheduleTasks(T ... tasks);
}
