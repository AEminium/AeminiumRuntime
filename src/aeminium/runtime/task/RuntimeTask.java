package aeminium.runtime.task;

import java.util.Collection;
import java.util.concurrent.Callable;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.Task;
import aeminium.runtime.statistics.Statistics;

public interface RuntimeTask extends Task, Callable<Object> {
	public void taskCompleted();
	
	public Collection<Hints> getHints();
	
	public void setStatistics(Statistics statistics);
	
	public Statistics getStatistics();
	
	public Body getBody();
	
	public boolean isCompleted();
	
	public void setCompleted();
	
	public void setData(Object object);
	
	public Object getData();
	
	public TaskDescription getDescription();
}
