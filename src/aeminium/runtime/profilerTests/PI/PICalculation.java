package aeminium.runtime.profilerTests.PI;

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
		
		boolean offlineProfiling = true;
		
		if (offlineProfiling)
		{
	        // On startup, JProfiler does not record any data. The various recording subsystems have to be
	        // switched on programatically.
	        //Controller.startCPURecording(true);
	        //Controller.startAllocRecording(true);
	        Controller.startThreadProfiling();
	        Controller.startVMTelemetryRecording();
	        Controller.startProbeRecording("aeminium.runtime.profiler.CountersProbe", true);
	        //Controller.startProbeRecording("aeminium.runtime.profiler.TaskDetailsProbe", true);
	        
	        File file = new File("/home/icorreia/snapshots/snap01.jps");
	        try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        Controller.saveSnapshotOnExit(file);
		}
		
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
