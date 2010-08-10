package aeminium.runtime.task;

import java.util.Collection;
import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.RuntimeScheduler;

public abstract class AbstractTask<T extends RuntimeTask> implements RuntimeTask {
	protected volatile Object result = UNSET;
	protected Body body;
	protected RuntimeGraph<T> graph;
	protected RuntimeScheduler<T> scheduler;
	protected final Collection<Hints> hints;
	protected static final Object UNSET = new Object() {
		@Override
		public String toString() {
			return "UNSET";
		}
	};
	protected int level = 0;
	
	public AbstractTask(Body body, Collection<Hints> hints, EnumSet<Flags> flags) {
		this.body = body;
		this.hints = hints;
	}
	
	@Override
	public Object call() throws Exception {
		try {
			body.execute(this);
		} catch (Throwable e) {
			setResult(e);
		} finally {
			@SuppressWarnings("unchecked")
			T Tthis = (T)this;
			taskFinished();
			if ( scheduler != null ) {
				scheduler.taskFinished(Tthis);
			}
		}
		return null;		
	}
	
	public final Body getBody() {
		return body;
	}
	
	public final Collection<Hints> getHints() {
		return hints;
	}
	
	@SuppressWarnings("unchecked")
	public void setScheduler(RuntimeScheduler scheduler) {
		this.scheduler = scheduler;
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
		while (result == UNSET ) ;
		if ( result == UNSET ) {
			throw new RuntimeError("Result has either not been set or already retrieved");
		}
		Object value = result;
		result = UNSET;
		return value;
	}

	protected final void setLevel(int level) {
		this.level = level;
	}
	
	public final int getLevel() {
		return this.level;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void taskCompleted() {
		graph.taskCompleted((T)this);
	}
}
