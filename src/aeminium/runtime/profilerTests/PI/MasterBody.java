package aeminium.runtime.profilerTests.PI;

import java.util.Collection;
import java.util.LinkedList;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

class BodySum implements Body
{
	MasterBody parent;
	
	public BodySum(MasterBody parent)
	{
		this.parent = parent;
	}

	@Override
	public void execute(Runtime rt, Task current)
		throws Exception
	{
		double value = 0;
		
		/* Sums the values from all the tasks, dividing it to get the mean. */
		for (WorkerBody body : this.parent.workersBodies)
			value += body.value;
		
		value /= MasterBody.NO_TASKS;
		
		/* If we already have values for the parent, we have to calculate the
		 * mean of both. Otherwise, just assign the calculated PI value.
		 */
		if (this.parent.PIValue > 0)
			this.parent.PIValue = (this.parent.PIValue + value) / 2;
		else
			this.parent.PIValue = value;
	}
}

public class MasterBody implements Body
{
	public static final long DARTS = 100000000;     /* number of throws at dartboard */
	public static final long NO_TASKS = 4;

	public volatile double PIValue = 0;
	public LinkedList<WorkerBody> workersBodies;
	
	public MasterBody()
	{
		this.workersBodies = new LinkedList<WorkerBody>();
	}

	@Override
	public void execute(Runtime rt, Task current)
	throws Exception
	{	
		Collection<Task> workersTasks = new LinkedList<Task>();
		
		for (long j = 0; j < NO_TASKS; j++)
		{
			WorkerBody body = new WorkerBody(MasterBody.DARTS);
			Task task = rt.createNonBlockingTask(body, Runtime.NO_HINTS);
			rt.schedule(task, Runtime.NO_PARENT, Runtime.NO_DEPS);

			workersBodies.add(body);
			workersTasks.add(task);
		}
		
		BodySum bodySum = new BodySum(this);
		Task taskSum = rt.createNonBlockingTask(bodySum, Runtime.NO_HINTS);
		rt.schedule(taskSum, Runtime.NO_PARENT, workersTasks);
	}
}
