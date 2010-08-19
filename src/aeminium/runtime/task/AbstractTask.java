package aeminium.runtime.task;

import aeminium.runtime.Body;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.implementations.AbstractRuntime;
import aeminium.runtime.scheduler.AeminiumThread;

public abstract class AbstractTask<T extends RuntimeTask> implements RuntimeTask {
	protected static final Object UNSET = new Object() {
		@Override
		public String toString() {
			return "UNSET";
		}
	};
	protected volatile Object result = UNSET;  // could merge result with body  
	protected Body body;
	public final short hints;
	public short level;
	public Thread waiter;    // we could same this and just mention that there is someone waiting

	
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

	@Override
	public final void setResult(Object result) {
		if ( result == null ) {
			throw new RuntimeError("Cannot set result to 'null'.");
		}
		this.result = result;
	}
	
	@Override
	public final Object getResult() {
		if ( isCompleted() ) {
			return result;
		} else {
			Thread thread = Thread.currentThread();
			if ( thread instanceof AeminiumThread ) {
				((AeminiumThread)thread).progressToCompletion(this);
			} else {
				synchronized (this) {
					while ( !isCompleted() ) {
						waiter = thread;
						try {
							this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return result;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void taskCompleted() {
		AbstractRuntime.graph.taskCompleted((T)this);
	}
}
