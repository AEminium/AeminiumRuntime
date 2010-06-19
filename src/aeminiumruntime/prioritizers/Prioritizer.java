package aeminiumruntime.prioritizers;

import java.util.List;

import aeminiumruntime.RuntimeTask;

public interface Prioritizer {
		public RuntimeTask getNext(List<RuntimeTask> nextList);
}
