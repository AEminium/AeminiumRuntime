package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public interface ParallelizationDecider {
	/* Initial bootstrapping */

	public void setRuntime(Runtime rt);

	/* Returns whether a task be executed in parallel, or sequentially. */
	public boolean parallelize(ImplicitTask current);
}
