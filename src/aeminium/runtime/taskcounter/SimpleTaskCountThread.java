package aeminium.runtime.taskcounter;

import java.util.concurrent.ThreadFactory;

import org.junit.internal.runners.statements.RunAfters;

public class SimpleTaskCountThread extends Thread implements TaskCountingThread {
	protected volatile long taskCount = 0;
	
	public static ThreadFactory getFactory(final RuntimeTaskCounter taskCounter) {
		return new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				SimpleTaskCountThread thread = new SimpleTaskCountThread(r);
				taskCounter.registerThread(thread);
				return thread;
			}
		};
	}
	
	public SimpleTaskCountThread(Runnable target) {
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
