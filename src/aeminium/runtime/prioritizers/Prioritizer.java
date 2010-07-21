package aeminium.runtime.prioritizers;

import java.util.List;

public interface Prioritizer {
		public <T> T getNext(List<T> nextList);
}
