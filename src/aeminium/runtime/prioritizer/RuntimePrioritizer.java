package aeminium.runtime.prioritizer;

import aeminium.runtime.implementations.implicitworkstealing.events.RuntimeEventManager;
import aeminium.runtime.task.RuntimeTask;

public interface RuntimePrioritizer<T extends RuntimeTask> {
	public void init(RuntimeEventManager eventManager);
	public void shutdown();
	public void scheduleTask(T task);
	public void taskFinished(T task);
	public void taskPaused(T task);
}
