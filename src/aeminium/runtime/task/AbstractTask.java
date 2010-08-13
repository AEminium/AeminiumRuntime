package aeminium.runtime.task;

import aeminium.runtime.Body;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.scheduler.RuntimeScheduler;

public abstract class AbstractTask<T extends RuntimeTask> implements RuntimeTask {
	protected volatile Object result = UNSET;
	protected Body body;
	public RuntimeGraph<T> graph;
	public RuntimeScheduler<T> scheduler;
	public final long hints;
	protected static final Object UNSET = new Object() {
		@Override
		public String toString() {
			return "UNSET";
		}
	};
	public int level;
	
	public AbstractTask(Body body, long hints) {
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
			taskFinished();
			@SuppressWarnings("unchecked")
			T Tthis = (T)this;
			if ( scheduler != null ) {
				scheduler.taskFinished(Tthis);
			}
		}
		return null;		
	}
	
	public final Body getBody() {
		return body;
	}
	
	
	@SuppressWarnings("unchecked")
	public final void setScheduler(RuntimeScheduler scheduler) {
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
	public final Object getResult() {
		//while (result == UNSET ) ;
		if ( result == UNSET ) {
			throw new RuntimeError("Result has either not been set or already retrieved");
		}
		Object value = result;
		result = UNSET;
		return value;
	}

//	protected final void setLevel(int level) {
//		this.level = level;
//	}
//	
//	public final int getLevel() {
//		return this.level;
//	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void taskCompleted() {
		graph.taskCompleted((T)this);
	}
}
