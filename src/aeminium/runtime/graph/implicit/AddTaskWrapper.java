package aeminium.runtime.graph.implicit;

import java.util.Collection;

import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.implicit.ImplicitTask;

public class AddTaskWrapper<T extends RuntimeTask> extends ImplicitTask {
	private final T task;
	private final Task parent;
	private final Collection<T> deps;
	
	public AddTaskWrapper(T task, Task parent, Collection<T> deps2) {
		super(null, null, Runtime.NO_HINTS, null);
		this.task = task;
		this.parent = parent;
		this.deps = deps2;
	}

	public T getTask() {
		return task;
	}

	public Task getParent() {
		return parent;
	}

	public Collection<T> getDeps() {
		return deps;
	}

	public String toString() {
		return "AddTaskWrapper";
	}
}
