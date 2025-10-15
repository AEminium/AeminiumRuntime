package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public class MaxLevel implements ParallelizationDecider {

	protected final int maxLevel  = Configuration.getProperty(getClass(), "maxLevelThreshold", 16);


	@Override
	public void setRuntime(Runtime rt) {
		// No need
	}

	@Override
	public boolean parallelize(ImplicitTask current) {
		if (current == null) return true;
		return current.level < maxLevel;
	}

}
