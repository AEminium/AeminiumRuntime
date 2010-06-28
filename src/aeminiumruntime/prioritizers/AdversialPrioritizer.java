package aeminiumruntime.prioritizers;

import java.util.List;


public class AdversialPrioritizer implements Prioritizer {

	@Override
	public <T> T getNext(List<T> nextList) { 
		return nextList.get(nextList.size()-1);
	}

}
