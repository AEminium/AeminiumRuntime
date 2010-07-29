package aeminium.runtime.task;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.statistics.Statistics;

public abstract class AbstractTask<T extends RuntimeTask> implements RuntimeTask {
	protected volatile Object result = UNSET;
	protected Body body;
	protected final Collection<Hints> hints;
	protected RuntimeGraph<T> graph;
	protected Map<String, Object> data;
	protected final EnumSet<Flags> flags;
	protected boolean hasRun = false;
	protected static final Object UNSET = new Object() {
		@Override
		public String toString() {
			return "UNSET";
		}
	};
	protected int level = 0;
	protected RuntimeScheduler<T> scheduler;
	
	public AbstractTask(RuntimeGraph<T> graph, Body body, Collection<Hints> hints, EnumSet<Flags> flags) {
		this.body = body;
		this.graph = graph;
		this.hints = hints;
		this.flags = flags;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setScheduler(RuntimeScheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	@Override
	public void setGraph(RuntimeGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public Object call() throws Exception {
		try {
			body.execute(this);
		} catch (Exception e) {
			setResult(e);
			System.out.println("bad " + e);
			e.printStackTrace();
		} finally {
			graph.taskFinished((T) this);
			scheduler.taskFinished((T) this);
			hasRun = true;
		}
		return null;		
	}
	
	public Body getBody() {
		return body;
	}
	
	public Collection<Hints> getHints() {
		return hints;
	}
	
	@Override
	public void setResult(Object result) {
		if ( result == null ) {
			throw new RuntimeError("Cannot set result to 'null'.");
		}
		this.result = result;
	}
	
	@Override
	public Object getResult() {
		if ( result == UNSET ) {
			throw new RuntimeError("Result has either not been set or already retrieved");
		}
		//while ( result == null ) ;
		Object value = result;
		result = UNSET;
		return value;
	}
	
	@Override
	public TaskDescription<T> getDescription() {
		return graph.getTaskDescription((T) this);
	}

	@Override
	public void setData(String key, Object value) {
		if ( data == null) {
			this.data = new HashMap<String, Object>();
		}
		this.data.put(key, value);
	}
	
	public Object getData(String key) {
		if (data == null) {
			return null;
		} else {
			return data.get(key);
		}
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	@Override
	public void taskCompleted() {		
	}
}
