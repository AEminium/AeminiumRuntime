package aeminium.runtime.implementations.forkjoin.tasks;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import aeminium.runtime.Body;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.forkjoin.FJRuntime;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTaskState;

@SuppressWarnings("serial")
public class FJTask extends RecursiveTask<Void> implements Task {

	protected static final Object UNSET = new Object()
	{
		@Override
		public String toString()
		{
			return "UNSET";
		}
	};
	
	protected volatile Object result = UNSET;
	public Body body;
	public ImplicitTaskState state = ImplicitTaskState.UNSCHEDULED;  // could be a byte instead of a reference
	public volatile int depCount;
	public volatile int childCount;
	public List<FJTask> dependents;
	public FJTask parent;
	public FJRuntime runtime;
	
	public FJTask(Body b, short hints, FJRuntime rt) {
		this.body = b;
	}
	
	@Override
	public void setResult(Object value) {
		result = value;
	}

	@Override
	public Object getResult() {
		while (state != ImplicitTaskState.COMPLETED) {
			while (state == ImplicitTaskState.WAITING_FOR_CHILDREN) {
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
				}
			}
			this.join();
		}
		return result;
	}

	@Override
	protected Void compute() {
		runtime.handleStartOfTask(this);
		try {
			perform();
		} catch (Exception e) {
			runtime.receiveException(this, e);
		}
		runtime.handleEndOfTask(this);
		return null;
	}
	
	protected void perform() throws Exception {
		body.execute(runtime, this);
	}


}
