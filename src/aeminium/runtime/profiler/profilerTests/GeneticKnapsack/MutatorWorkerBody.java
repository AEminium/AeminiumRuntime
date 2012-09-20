package aeminium.runtime.profiler.profilerTests.GeneticKnapsack;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class MutatorWorkerBody implements Body {

	private int taskNumber;
	private Indiv[] parents;
	private double prob_mut;
	
	MutatorWorkerBody(int number, int half, Indiv[] parents, double prob_mut) {
		this.taskNumber = number;
		this.parents = parents;
		this.prob_mut = prob_mut;
	}
	
	@Override
	public void execute(Runtime rt, Task current) throws Exception {
		
		if (Knapsack.rand.nextDouble() < prob_mut)
			mutate(parents[taskNumber]);
		
	}
	
	private void mutate(Indiv indiv) {
		int sw = Knapsack.rand.nextInt(Knapsack.cromSize);
		indiv.has[sw] = !indiv.has[sw];
	}
	
}
