package aeminium.runtime.profiler.profilerTests.PI;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class WorkerBody implements Body
{
	private long darts = 0;
	public double value = 0;
	private long workerID = 0;
	
	public WorkerBody(long darts, long id)
	{
		this.darts = darts;
		this.workerID = id;
	}
	

	@Override
	public void execute(Runtime rt, Task current) throws Exception
	{
		double x_coord, y_coord, r; 
		long score = 0;
		MersenneTwisterFast random = new MersenneTwisterFast();

		/* "throw darts at board" */
		for (long n = 1; n <= darts; n++)
		{
			/* generate random numbers for x and y coordinates */
			r = random.nextDouble();
			x_coord = (2.0 * r) - 1.0;
			r = random.nextDouble();
			y_coord = (2.0 * r) - 1.0;

			/* if dart lands in circle, increment score */
			if ((x_coord*x_coord + y_coord*y_coord) <= 1.0)
				score++;
		}

		/* Calculate PI. */
		this.value = 4.0 * (double)score/(double)darts;
	}

}
