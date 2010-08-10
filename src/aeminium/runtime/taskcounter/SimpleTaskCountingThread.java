package aeminium.runtime.taskcounter;

import java.util.concurrent.ThreadFactory;

public class SimpleTaskCountingThread extends Thread implements TaskCountingThread {
	protected volatile long taskCount = 0;
	
	public static ThreadFactory getFactory(final RuntimeTaskCounter taskCounter) {
		return new ThreadFactory() {
			
			@Override
			public final Thread newThread(Runnable r) {
				SimpleTaskCountingThread thread = new SimpleTaskCountingThread(r);
				taskCounter.registerThread(thread);
				return thread;
			}
		};
	}
	
	public SimpleTaskCountingThread(Runnable target) {
		super(target);
	}


	@Override
 	public long getDelta() {
		return taskCount;
	}

	@Override
	public void tasksAdded(int delta) {
		taskCount += delta;
	}

	@Override
	public void tasksCompleted(int delta) {
		taskCount -= delta;
	}

}
