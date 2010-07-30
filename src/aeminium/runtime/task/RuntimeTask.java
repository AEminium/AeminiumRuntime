package aeminium.runtime.task;

import java.util.Collection;
import java.util.concurrent.Callable;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.Task;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.scheduler.RuntimeScheduler;

public interface RuntimeTask extends Task, Callable<Object> {
	public void taskCompleted();
	
	public Collection<Hints> getHints();
	
	public Body getBody();
	
	public void setData(String key, Object value);
	
	public Object getData(String key);
	
	public TaskDescription getDescription();
	
	public int getLevel();
	
	public void setScheduler(RuntimeScheduler scheduler);
	
	public void setGraph(RuntimeGraph graph);
}
