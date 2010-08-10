package aeminium.runtime.prioritizer;

import java.util.Collection;

import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.taskcounter.RuntimeTaskCounter;

public interface RuntimePrioritizer<T extends RuntimeTask> {
	public void init(RuntimeTaskCounter tc);
	public void shutdown();
	public void scheduleTasks(Collection<T> tasks);
	public void scheduleTask(T task);
	public void taskFinished(T task);
	public void taskPaused(T task);
}
