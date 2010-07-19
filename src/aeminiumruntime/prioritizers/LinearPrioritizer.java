package aeminiumruntime.prioritizers;

import java.util.List;

public class LinearPrioritizer implements Prioritizer {

	@Override
	public <T> T getNext(List<T> nextList) { 
		return nextList.get(0);
	}

}
