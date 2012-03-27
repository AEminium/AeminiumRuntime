package tests.PI;

import aeminium.runtime.*;
import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.Factory;


public class PICalculation
{	
	public static void main(String args[])
	{
		Runtime rt = Factory.getRuntime();
		rt.init();
		
		int noMasterTasks = Configuration.getProcessorCount();
		
		MasterBody[] masterArray = new MasterBody[noMasterTasks];
		
		for (int i = 0; i < noMasterTasks; i++)
		{
			masterArray[i] = new MasterBody();	
			Task taskMain = rt.createNonBlockingTask(masterArray[i], Runtime.NO_HINTS);
			rt.schedule(taskMain, Runtime.NO_PARENT, Runtime.NO_DEPS);
		}

		rt.shutdown();
		
		double pi = 0;
		
		System.out.println("Counting " + noMasterTasks);
		
		for (int i = 0; i < noMasterTasks; i++)
			pi += masterArray[i].PIValue;
		
		System.out.println(pi/noMasterTasks);
	}
}
