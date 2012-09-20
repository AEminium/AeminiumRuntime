package aeminium.runtime.profiler.profilerTests.PI;

import java.io.File;
import java.io.IOException;

import com.jprofiler.api.agent.Controller;

import aeminium.runtime.*;
import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.Factory;


public class PICalculation
{	
	public static void main(String args[])
	{
		/* Arguments check up. */
		if (args.length != 5)
		{
			System.out.println("Usage: java -jar PI.exe [boolean: CountersProbe] [boolean: TaskDetailsProbe] " +
						"[task length] [no. tasks] [output file]");
			System.exit(0);
		}
		
		boolean offlineProfiling = Boolean.parseBoolean(args[0]);
		boolean taskDetails = Boolean.parseBoolean(args[1]);
		
		if (offlineProfiling)
		{
			/* Activation of profiling options according to the parameters given. */
			//Controller.startCPURecording(true);
			Controller.startVMTelemetryRecording();
	        Controller.startThreadProfiling();
	        Controller.startProbeRecording("aeminium.runtime.profiler.CountersProbe", true);
	        
	        if (taskDetails)
	        	Controller.startProbeRecording("aeminium.runtime.profiler.TaskDetailsProbe", true);
	        
	        try 
	        {
	        	File file = new File(args[4]);
				file.createNewFile();
				Controller.saveSnapshotOnExit(file);
				
			} catch (IOException e)
			{
				System.out.println("File error: " + e.getMessage());
				System.exit(-1);
			}
		}
        
		/* Execution of the program itself. */
		long startTime = System.nanoTime();
		
		Runtime rt = Factory.getRuntime();
		rt.init();
		
		int noMasterTasks = Configuration.getProcessorCount();
		
		MasterBody[] masterArray = new MasterBody[noMasterTasks];
		
		for (int i = 0; i < noMasterTasks; i++)
		{
			masterArray[i] = new MasterBody(Long.parseLong(args[2]),
								Long.parseLong(args[3]));	
			
			Task taskMain = rt.createNonBlockingTask(masterArray[i], Runtime.NO_HINTS);
			rt.schedule(taskMain, Runtime.NO_PARENT, Runtime.NO_DEPS);
		}

		rt.shutdown();
		
		double pi = 0;
		
		System.out.println("Counting " + noMasterTasks);
		
		for (int i = 0; i < noMasterTasks; i++)
			pi += masterArray[i].PIValue;
		
		System.out.println(pi/noMasterTasks);
		
        long endTime = System.nanoTime();
        
        long finalTime = endTime - startTime;
        
        System.out.println("Time: " + finalTime);
	}
}
