package aeminium.runtime.futures;

import java.util.Arrays;

import aeminium.runtime.Task;

/* A Future Task with Parent. */
public class FutureChild<T> extends HollowFuture<T> {
	Task parent;

	public FutureChild(FutureBody<T> b, Task p, HollowFuture<?>... futures) {
		parent = p;
		body = b;
		RuntimeManager.submit(this, parent, prepareDependencies(Arrays.asList(futures)));
	}
}
