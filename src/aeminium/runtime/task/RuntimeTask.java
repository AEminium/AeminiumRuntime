package aeminium.runtime.task;

import java.util.concurrent.Callable;

import aeminium.runtime.Body;
import aeminium.runtime.Task;

public interface RuntimeTask extends Task, Callable<Object> {
	
	public Body getBody();
	
	public void taskFinished();
	
	public void taskCompleted();
}
