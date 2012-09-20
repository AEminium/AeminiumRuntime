package aeminium.runtime.profiler.profilerTests.GeneticKnapsack;

import aeminium.runtime.*;
import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Factory;


public class Recombiner {

	public void execute(int half, Indiv[] parents, double prob_rec)
	{	
		/* RUNTIME INITIALIZATION */
		Runtime rt = Factory.getRuntime();
		rt.init();
		
		for (int i = 0; i < half; i++)
		{
			Task task = rt.createNonBlockingTask(new RecombinerWorkerBody(i, half, parents, prob_rec), Runtime.NO_HINTS);
			rt.schedule(task, Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		
		/* RUNTIME SHUTDOWN */
		rt.shutdown();
	}

}
