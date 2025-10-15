package aeminium.runtime.futures.codegen;

import aeminium.runtime.Task;

public interface Expression<T> {
	public T evaluate(Task t);
}
