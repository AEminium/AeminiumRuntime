package aeminium.runtime.futures;

import java.util.Arrays;
import java.util.Collection;

import aeminium.runtime.Runtime;

public class Future<T> extends HollowFuture<T>{

	public Future(FutureBody<T> b) {
		body = b;
		RuntimeManager.submit(this, Runtime.NO_PARENT, Runtime.NO_DEPS);
	}

	public Future(FutureBody<T> b, HollowFuture<?>... futures) {
		body = b;
		RuntimeManager.submit(this, Runtime.NO_PARENT, prepareDependencies(Arrays.asList(futures)));
	}

	public Future(FutureBody<T> b, Collection<HollowFuture<?>> futures) {
		body = b;
		RuntimeManager.submit(this, Runtime.NO_PARENT, prepareDependencies(futures));
	}
}
