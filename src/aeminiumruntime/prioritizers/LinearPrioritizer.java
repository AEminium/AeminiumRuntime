package aeminiumruntime.prioritizers;

import java.util.List;

import aeminiumruntime.RuntimeTask;

public class LinearPrioritizer implements Prioritizer {

	@Override
	public RuntimeTask getNext(List<RuntimeTask> nextList) {
		return nextList.get(0);
	}

}
