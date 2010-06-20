package aeminiumruntime.queue;

public enum QTaskState {
	WAITING_FOR_DEPENDENCIES,
	RUNNIG,
	WAITING_FOR_CHILDREN,
	FINISHED
}
