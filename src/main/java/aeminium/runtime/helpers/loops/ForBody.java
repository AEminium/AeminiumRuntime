package aeminium.runtime.helpers.loops;

import aeminium.runtime.Task;
import aeminium.runtime.Runtime;

public interface ForBody<T> {
	public void iterate(T i, Runtime rt, Task current);
}
