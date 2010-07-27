package aeminium.runtime.task.implicit2;

public enum ImplicitTaskState2 {
	UNSCHEDULED,
	WAITING_FOR_DEPENDENCIES,
	RUNNING,
	WAITING_FOR_CHILDREN,
	COMPLETED
}
