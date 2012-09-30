package aeminium.runtime.profiler.profilerTests.CompilerOriginal;

import java.io.File;
import java.io.IOException;

import com.jprofiler.api.agent.Controller;

public class Fibonacci
{
	public static int f(int n)
	{
		if (n < 2)
			return n;
		return f(n-1) + f(n-2);
	}

	public static void main(String[] args)
	{
		/* Activation of profiling options according to the parameters given. */
		Controller.startVMTelemetryRecording();
        Controller.startThreadProfiling();
        Controller.startProbeRecording("aeminium.runtime.profiler.CountersProbe", true);
        
        try 
        {
        	File file = new File(args[0]);
			file.createNewFile();
			Controller.saveSnapshotOnExit(file);
			
		} catch (IOException e)
		{
			System.out.println("File error: " + e.getMessage());
			System.exit(-1);
		}
        
		System.out.println(f(50));
	}
}
