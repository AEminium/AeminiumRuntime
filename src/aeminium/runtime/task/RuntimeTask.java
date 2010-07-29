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
	
	public void setData(String key, Object value);
	
	public Object getData(String key);
	
	public TaskDescription getDescription();
	
	public void setLevel(int level);
	
	public int getLevel();
}
