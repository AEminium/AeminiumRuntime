package aeminiumruntime.prioritizers;

import java.util.List;


public class SmartPrioritizer implements Prioritizer {

	PrioritizableTaskGraph graph;
	
	public SmartPrioritizer(PrioritizableTaskGraph g) {
		graph = g;
	}
	
	@Override
	public <T> T getNext(List<T> nextList) { 
		if (nextList.size() == 0) return null;
		
		T bestTask = null;
		int bestScore = 0;
		int currentScore = 0;
		for (T task: nextList) {
			currentScore = getScoreOf(task);
			if (bestTask == null || currentScore > bestScore) {
				bestTask = task;
				bestScore = currentScore;
			} 
		}
		return bestTask;
	}

	public <T> int getScoreOf(T bestTask) {
		return this.graph.countDependencies(bestTask);
	}

}