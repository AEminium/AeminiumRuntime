package aeminium.runtime.prioritizer;

import java.util.Collection;

import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.task.RuntimeTask;

public interface RuntimePrioritizer<T extends RuntimeTask> {
	public void init(RuntimeEventManager eventManager);
	public void shutdown();
	public void scheduleTasks(Collection<T> tasks);
	public void scheduleTask(T task);
	public void taskFinished(T task);
	public void taskPaused(T task);
}
