package aeminiumruntime.task;

import java.util.Collection;
import java.util.concurrent.Callable;

import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.Task;
import aeminiumruntime.statistics.Statistics;

public interface RuntimeTask extends Task, Callable<Object> {
	public void taskCompleted();
	
	public Collection<Hint> getHints();
	
	public void setStatistics(Statistics statistics);
	
	public Statistics getStatistics();
	
	public Body getBody();
	
	public TaskDescription getDescription();
}
