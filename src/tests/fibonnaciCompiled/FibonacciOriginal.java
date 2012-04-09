package tests.fibonnaciCompiled;

import com.jprofiler.api.agent.Controller;
import java.io.File;
import java.io.IOException;

public class FibonacciOriginal 
{
    public long fib(int n)
    {
        if (n <= 1)
        	return n;
        
        return fib(n-1) + fib(n-2);
    }

    public static void main(String[] args) 
    {
    	FibonacciOriginal fibonacci = new FibonacciOriginal();
        int n = Integer.parseInt(args[0]);
		boolean offlineProfiling = Boolean.parseBoolean(args[1]);
		boolean taskDetails = Boolean.parseBoolean(args[2]);
		
		if (args.length == 3)
		
		if (offlineProfiling)
		{
	        Controller.startThreadProfiling();
	        Controller.startVMTelemetryRecording();
	        Controller.startProbeRecording("aeminium.runtime.profiler.CountersProbe", true);
	        
	        if (taskDetails)
	        	Controller.startProbeRecording("aeminium.runtime.profiler.TaskDetailsProbe", true);
	        
	        File file = new File(args[3]);
	        try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        Controller.saveSnapshotOnExit(file);
		}
        
		long startTime = System.nanoTime();
		
        System.out.println(fibonacci.fib(n));
        
        long endTime = System.nanoTime();
        
        long finalTime = endTime - startTime;
        
        System.out.println("Time: " + finalTime);
    }

}