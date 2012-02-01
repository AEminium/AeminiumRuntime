package aeminium.runtime.tests;

import java.util.Arrays;

import aeminium.runtime.*;
import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Factory;

class FibBodySum implements Body
{
	FibBody parent;
	
	public FibBodySum(FibBody parent)
	{
		this.parent = parent;
	}

	@Override
	public void execute(Runtime rt, Task current)
		throws Exception
	{
		this.parent.value = this.parent.body1.value + this.parent.body2.value;
	}
}

class FibBody implements Body
{
	public int n;
	public volatile int value;
	
	public FibBody body1;
	public FibBody body2;
	
	public FibBody(int n)
	{
		this.n = n;
	}
	
	@Override
	public void execute(Runtime rt, Task current)
		throws Exception
	{
		if (this.n < 2)
			this.value = n;
		else
		{
			this.body1 = new FibBody(this.n - 1);
			Task task1 = rt.createNonBlockingTask(this.body1, Runtime.NO_HINTS);
			rt.schedule(task1, current, Runtime.NO_DEPS);
			
			this.body2 = new FibBody(this.n - 2);	
			Task task2 = rt.createNonBlockingTask(this.body2, Runtime.NO_HINTS);
			rt.schedule(task2, current, Runtime.NO_DEPS);

			FibBodySum bodySum = new FibBodySum(this);
			Task taskSum = rt.createNonBlockingTask(bodySum, Runtime.NO_HINTS);
			rt.schedule(taskSum, current, Arrays.asList(new Task[] {task1, task2}));
		}
	}
}

public class FibonnaciGetless
{

	public static void main(String args[])
	{
		Runtime rt = Factory.getRuntime();
		rt.init();
		
		FibBody bodyMain = new FibBody(30);	
		Task taskMain = rt.createNonBlockingTask(bodyMain, Runtime.NO_HINTS);
		rt.schedule(taskMain, Runtime.NO_PARENT, Runtime.NO_DEPS);
		
		rt.shutdown();
		
		System.out.println(bodyMain.value);
	}
}
