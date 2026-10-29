package aeminium.runtime.futures;

import aeminium.runtime.Task;

public class FutureWrapper<T> extends HollowFuture<T> {
	public FutureWrapper(Task t) {
		task = t;
	}

	@SuppressWarnings("unchecked")
	public T get() {
		return (T) task.getResult();
	}
}
