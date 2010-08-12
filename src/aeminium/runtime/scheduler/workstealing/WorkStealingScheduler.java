package aeminium.runtime.scheduler.workstealing;

import aeminium.runtime.task.RuntimeTask;

public interface WorkStealingScheduler<T extends RuntimeTask> {
	public void registerThread(WorkerThread<T> thread);
	public void unregisterThread(WorkerThread<T> thread);
	public T scanQueues(WorkerThread<T> thread);
	public void parkThread(WorkerThread<T> thread);
}
