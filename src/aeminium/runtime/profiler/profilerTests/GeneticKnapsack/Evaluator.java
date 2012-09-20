package aeminium.runtime.profiler.profilerTests.GeneticKnapsack;

import aeminium.runtime.*;
import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Factory;


public class Evaluator {

	public void execute(int number, Indiv[] pop, boolean isToGen)
	{	
		/* RUNTIME INITIALIZATION */
		Runtime rt = Factory.getRuntime();
		rt.init();
		
		for (int i = 0; i < number; i++)
		{
			Task task = rt.createNonBlockingTask(new EvaluatorWorkerBody(i,pop, isToGen), Runtime.NO_HINTS);
			rt.schedule(task, Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		
		/* RUNTIME SHUTDOWN */
		rt.shutdown();
	}

}
