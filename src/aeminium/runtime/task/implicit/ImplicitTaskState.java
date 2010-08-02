package aeminium.runtime.task.implicit;

public enum ImplicitTaskState {
	UNSCHEDULED,
	WAITING_FOR_DEPENDENCIES,
	RUNNING,
	WAITING_FOR_CHILDREN,
	COMPLETED
}
