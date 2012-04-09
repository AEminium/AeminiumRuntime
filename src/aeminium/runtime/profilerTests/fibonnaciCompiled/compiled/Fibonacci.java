package aeminium.runtime.profilerTests.fibonnaciCompiled.compiled;
import aeminium.runtime.AeminiumHelper;

import com.jprofiler.api.agent.Controller;
import java.io.File;
import java.io.IOException;

class Fibonacci {
  public static int f(  int n){
    if (n < 2)     return n;
 else     return f(n - 1) + f(n - 2);
  }
  public static void main(  String[] args){

	boolean offlineProfiling, taskDetails;
	String fileName;
  
	if (args.length == 3) 
	{
		offlineProfiling = Boolean.parseBoolean(args[0]);
		taskDetails = Boolean.parseBoolean(args[1]);
		fileName = args[2];
	} else
	{
		offlineProfiling = false;
		taskDetails = false;
		fileName = "snapshot.jps";
	}
	
	if (offlineProfiling)
	{
		Controller.startThreadProfiling();
		Controller.startVMTelemetryRecording();
		Controller.startProbeRecording("aeminium.runtime.profiler.CountersProbe", true);
		
		if (taskDetails)
			Controller.startProbeRecording("aeminium.runtime.profiler.TaskDetailsProbe", true);
		
		File file = new File(fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Controller.saveSnapshotOnExit(file);
	}
	
	long startTime = System.nanoTime();

    AeminiumHelper.init();
    new Fibonacci_main().schedule(AeminiumHelper.NO_DEPS,args);
    AeminiumHelper.shutdown();
	
	long endTime = System.nanoTime();
	
	long finalTime = endTime - startTime;
	
	System.out.println("Time: " + finalTime);
	
  }
}