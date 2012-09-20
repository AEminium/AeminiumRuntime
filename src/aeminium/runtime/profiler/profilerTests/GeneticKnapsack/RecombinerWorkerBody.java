package aeminium.runtime.profiler.profilerTests.GeneticKnapsack;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class RecombinerWorkerBody implements Body {

	private int taskNumber;
	private int half;
	private Indiv[] parents;
	private double prob_rec;
	
	RecombinerWorkerBody(int number, int half, Indiv[] parents, double prob_rec) {
		this.taskNumber = number;
		this.half = half;
		this.parents = parents;
		this.prob_rec = prob_rec;
	}
	
	@Override
	public void execute(Runtime rt, Task current) throws Exception {
		
		if (Knapsack.rand.nextDouble() < prob_rec) {
			recombine(parents, taskNumber, half+taskNumber);
		}
		
	}
	
	private void recombine(Indiv[] parents, int a, int b) {
		Indiv n1 = new Indiv(Knapsack.cromSize);
		Indiv n2 = new Indiv(Knapsack.cromSize);
		int cutPos = Knapsack.rand.nextInt(Knapsack.cromSize);
		for (int k = 0; k < Knapsack.cromSize; k++) {
			if (k < cutPos) {
				n1.add(k,parents[a].has[k]);
				n2.add(k,parents[b].has[k]);
			} else {
				n1.add(k,parents[b].has[k]);
				n2.add(k,parents[a].has[k]);
			}
			
		}
		parents[a] = n1;
		parents[b] = n2;
	}
	
}
