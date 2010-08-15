package aeminium.runtime.task;

import aeminium.runtime.Body;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.implementations.AbstractRuntime;

public abstract class AbstractTask<T extends RuntimeTask> implements RuntimeTask {
	protected volatile Object result = UNSET;  // merge result with body to because 
	protected Body body;
	public final short hints;
	public short level;
	protected static final Object UNSET = new Object() {
		@Override
		public String toString() {
			return "UNSET";
		}
	};
	
	public AbstractTask(Body body, short hints) {
		this.body = body;
		this.hints = hints;
	}
	
	@Override
	public Object call() throws Exception {
		try {
			body.execute(AbstractRuntime.runtime, this);
		} catch (Throwable e) {
			setResult(e);
		} finally {
			taskFinished();
			@SuppressWarnings("unchecked")
			T Tthis = (T)this;
			if ( AbstractRuntime.scheduler != null ) {
				AbstractRuntime.scheduler.taskFinished(Tthis);
			}
		}
		return null;		
	}
	
	public final Body getBody() {
		return body;
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
		
		if ( result == UNSET ) {
			throw new RuntimeError("Result has either not been set or already retrieved");
		}
		Object value = result;
		result = UNSET;
		return value;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void taskCompleted() {
		AbstractRuntime.graph.taskCompleted((T)this);
	}
}
