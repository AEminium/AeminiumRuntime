package aeminium.runtime.futures;

import java.util.ArrayList;
import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;


/* The Base Class for all Futures, containing the task and the dependencies. */
public abstract class HollowFuture<T> implements Body {

	public T it;
	public Task task;
	public DataGroup dg;
	protected FutureBody<T> body;

	protected Collection<Task> prepareDependencies(Collection<HollowFuture<?>> futures) {
		Collection<Task> c = new ArrayList<Task>();
		for(HollowFuture<?> f : futures) {
			c.add(f.task);
		}
		return c;
	}

	public T get() {
		task.getResult();
		return it;
	}

	@Override
	public void execute(Runtime rt, Task current) throws Exception {
		RuntimeManager.currentTask.set(current);
		it = body.evaluate(current);
	}
}
