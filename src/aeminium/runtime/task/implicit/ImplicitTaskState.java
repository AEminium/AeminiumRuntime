package aeminium.runtime.task.implicit;

public enum ImplicitTaskState {
	WAITING_FOR_DEPENDENCIES,
	RUNNING,
	WAITING_FOR_CHILDREN,
	FINISHED
}