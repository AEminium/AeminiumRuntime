package aeminium.runtime.profiler.profilerTests.PI;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import com.jprofiler.api.agent.Controller;

import aeminium.runtime.*;
import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Factory;

public class PICorrected
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
		
		long NO_TASKS = Long.parseLong(args[3]);
		long DARTS = Long.parseLong(args[2]);
		LinkedList<WorkerBody> workersBodies = new LinkedList<WorkerBody>();
		
		Collection<Task> workersTasks = new LinkedList<Task>();
		
		for (long j = 0; j < NO_TASKS; j++)
		{
			WorkerBody body = new WorkerBody(DARTS, j);
			Task task = rt.createNonBlockingTask(body, Runtime.NO_HINTS);
			rt.schedule(task, Runtime.NO_PARENT, Runtime.NO_DEPS);

			workersBodies.add(body);
			workersTasks.add(task);
		}
		
		BodySum bodySum = new BodySum(workersBodies, NO_TASKS);
		Task taskSum = rt.createNonBlockingTask(bodySum, Runtime.NO_HINTS);
		rt.schedule(taskSum, Runtime.NO_PARENT, workersTasks);

		rt.shutdown();
		
		System.out.println(bodySum.PIValue);
		
        long endTime = System.nanoTime();
        
        long finalTime = endTime - startTime;
        
        System.out.println("Time for " + NO_TASKS + ": " + finalTime/1000000.0);
	}
}

class BodySum implements Body
{
	private LinkedList<WorkerBody> workersBodies;
	private long NO_TASKS;
	public double PIValue;
	
	public BodySum(LinkedList<WorkerBody> workersBodies, long NO_TASKS)
	{
		this.workersBodies = workersBodies;
		this.NO_TASKS = NO_TASKS;
	}

	@Override
	public void execute(Runtime rt, Task current)
		throws Exception
	{
		double value = 0;
		
		/* Sums the values from all the tasks, dividing it to get the mean. */
		for (WorkerBody body : this.workersBodies)
			value += body.value;
		
		value /= this.NO_TASKS;
		
		/* If we already have values for the parent, we have to calculate the
		 * mean of both. Otherwise, just assign the calculated PI value.
		 */
		if (this.PIValue > 0)
			this.PIValue = (this.PIValue + value) / 2;
		else
			this.PIValue = value;
	}
}
