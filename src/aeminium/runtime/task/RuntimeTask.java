package aeminium.runtime.task;

import java.util.Collection;
import java.util.concurrent.Callable;

import aeminium.runtime.Body;
import aeminium.runtime.Hint;
import aeminium.runtime.Task;
import aeminium.runtime.statistics.Statistics;

public interface RuntimeTask extends Task, Callable<Object> {
	public void taskCompleted();
	
	public Collection<Hint> getHints();
	
	public void setStatistics(Statistics statistics);
	
	public Statistics getStatistics();
	
	public Body getBody();
	
	public TaskDescription getDescription();
}