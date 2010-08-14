package aeminium.runtime.scheduler;

import java.util.concurrent.ThreadFactory;

import aeminium.runtime.events.RuntimeEventManager;

public class AeminiumThread extends Thread {
	public volatile int taskCount = 0;
	protected final RuntimeEventManager eventManager;
	
	public AeminiumThread(RuntimeEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public AeminiumThread(Runnable runable, RuntimeEventManager eventManager) {
		super(runable);
		this.eventManager = eventManager;
	}
	
	public static ThreadFactory getFactory(final RuntimeEventManager eventManager) {
		return new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new AeminiumThread(r, eventManager);
			}
		};
	}

	@Override
	public void run() {
		eventManager.signalNewThread(this);
		super.run();
	}
	
	
}
