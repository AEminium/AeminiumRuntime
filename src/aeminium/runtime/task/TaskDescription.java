package aeminium.runtime.task;

public class TaskDescription<T extends RuntimeTask> {
	private final T task;
	private final long dependencyCount;
	private final long dependentTaskCount;
	
	protected TaskDescription(T task, long dependencyCount, long dependentTaskCount) {
		this.task = task;
		this.dependencyCount = dependencyCount;
		this.dependentTaskCount = dependentTaskCount;
	}
	
	public static <T extends RuntimeTask> TaskDescription<T> create(T task, long dependencyCount, long dependentTaskCount) {
		return new TaskDescription<T>(task, dependencyCount, dependentTaskCount);
	}

	public T getTask() {
		return task;
	}

	public long getDependencyCount() {
		return dependencyCount;
	}

	public long getDependentTaskCount() {
		return dependentTaskCount;
	}
}

