package aeminiumruntime.queue;

public enum QTaskState {
	WAITING_FOR_DEPENDENCIES,
	RUNNING,
	WAITING_FOR_CHILDREN,
	FINISHED
}
