package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public class StackSize implements ParallelizationDecider {
	ImplicitWorkStealingRuntime rt;

	protected final int maxStackSize  = Configuration.getProperty(getClass(), "maxStackSize", 13);

	@Override
	public void setRuntime(Runtime rt) {
		this.rt = (ImplicitWorkStealingRuntime) rt;
	}

	@Override
	public boolean parallelize(ImplicitTask current) {
		return Thread.currentThread().getStackTrace().length < maxStackSize;
	}

}
