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
import aeminium.runtime.statistics.Statistics;

public abstract class AbstractTask<T extends RuntimeTask> implements RuntimeTask {
	protected volatile Object result;
	//protected AtomicReference<Object> result = new AtomicReference<Object>();
	protected final Body body;
	protected final Collection<Hints> hints;
	protected final RuntimeGraph<T> graph;
	protected Statistics statistics;
	protected Map<String, Object> data;
	protected final EnumSet<Flags> flags;
	protected boolean hasRun = false;
	
	public AbstractTask(RuntimeGraph<T> graph, Body body, Collection<Hints> hints, EnumSet<Flags> flags) {
		this.body = body;
		this.graph = graph;
		this.hints = hints;
		this.flags = flags;
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
		while ( result == null ) ;
		return result;
	}
	
	@Override
	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}
	
	@Override
	public Statistics getStatistics() {
		return statistics;
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
	
	@Override
	public void taskCompleted() {		
	}
}
