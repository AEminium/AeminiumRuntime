package aeminium.runtime.task;

import java.util.concurrent.Callable;

import aeminium.runtime.Task;

public interface RuntimeTask extends Task, Callable<Object> {

	public boolean isCompleted();
	
	public void taskFinished();
	
	public void taskCompleted();
}
