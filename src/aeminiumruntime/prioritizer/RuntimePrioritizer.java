package aeminiumruntime.prioritizer;

import aeminiumruntime.task.RuntimeTask;

public interface RuntimePrioritizer<T extends RuntimeTask> {
	public void scheduleTasks(T ... tasks);
}
