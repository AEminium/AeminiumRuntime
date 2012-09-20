package aeminium.runtime.profiler.profilerTests.GeneticKnapsack;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class EvaluatorWorkerBody implements Body {

	private int taskNumber;
	private Indiv[] pop;
	private boolean isToGen;
	
	EvaluatorWorkerBody(int number, Indiv[] pop, boolean isToGen) {
		this.taskNumber = number;
		this.pop = pop;
		this.isToGen = isToGen;
	}
	
	@Override
	public void execute(Runtime rt, Task current) throws Exception {
		
		if (isToGen)
			pop[taskNumber] = genIndiv();
		
		pop[taskNumber].fitness = evaluate(pop[taskNumber]);
		
	}
	
	private double evaluate(Indiv indiv) {
		int[] ph = phenotype(indiv);
		int value = ph[0];
		int weight = ph[1];
		
		// Evaluation
		if (weight >= Knapsack.SIZE_LIMIT) {
			return 2.0;
		} else {
			return 1.0/(value); // Minimization problem.
		}
		
	}
	
	private int[] phenotype(Indiv indiv) {
		int value = 0;
		int weight = 0;
		for (int i=0; i< indiv.size; i++) {
			if (indiv.has[i]) {
				value += Knapsack.items[i].value;
				weight += Knapsack.items[i].weight;
			}
		}
		return new int[] {value, weight};
	}

	private Indiv genIndiv() {
		while (true) {
			int c = 0;
			Indiv e = new Indiv(Knapsack.numberOfItems);
			for(int i = 0; i < Knapsack.numberOfItems; i++) {
				boolean b = ( Knapsack.rand.nextDouble() < 0.01 );
				e.add(i, b);
				if (b) c++;
			}
			// Avoids empty backpacks
			if ( c > 0) {
				return e;
			}
		}
	}
	
}
