package aeminium.runtime.futures;

import aeminium.runtime.Task;

public interface FutureBody<T> {
	public T evaluate(Task t);
}
