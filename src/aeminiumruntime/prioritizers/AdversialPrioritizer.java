package aeminiumruntime.prioritizers;

import java.util.List;

import aeminiumruntime.RuntimeTask;

public class AdversialPrioritizer implements Prioritizer {

	@Override
	public RuntimeTask getNext(List<RuntimeTask> nextList) {
		return nextList.get(nextList.size()-1);
	}

}
