package aeminiumruntime.prioritizers;

import java.util.List;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.graphs.ParallelTaskGraph;

public class SmartPrioritizer implements Prioritizer {

	ParallelTaskGraph graph;
	
	public SmartPrioritizer(ParallelTaskGraph g) {
		graph = g;
	}
	
	@Override
	public RuntimeTask getNext(List<RuntimeTask> nextList) {
		if (nextList.size() == 0) return null;
		
		RuntimeTask bestTask = null;
		int bestScore = 0;
		int currentScore = 0;
		for (RuntimeTask task: nextList) {
			currentScore = getScoreOf(task);
			if (bestTask == null || currentScore > bestScore) {
				bestTask = task;
				bestScore = currentScore;
			} 
		}
		return bestTask;
	}

	private int getScoreOf(RuntimeTask bestTask) {
		return this.graph.countDependencies(bestTask);
	}

}
