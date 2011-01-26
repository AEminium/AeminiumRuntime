package aeminium.runtime.implementations.implicitworkstealing.scheduler;

import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.events.EventManager;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitBlockingTask;

public final class BlockingThreadPool {
	protected ImplicitWorkStealingRuntime rt;
	protected EventManager eventManager;
	private List<ImplicitBlockingTask> taskQueue;
	private int currentThreads;
	private int sleepingThreads;
	protected static int maxThreads               = Configuration.getProperty(BlockingThreadPool.class, "maxThreads", Runtime.getRuntime().availableProcessors()*2);
	protected final ImplicitBlockingTask FINISHED = new ImplicitBlockingTask(null, (short)0) {
		@Override
		public String toString() {
			return "FINISHED";
		}
	};
	
	public void init(ImplicitWorkStealingRuntime rt, EventManager eventManager) {
		this.rt   = rt;
		this.eventManager = eventManager;
		taskQueue = new LinkedList<ImplicitBlockingTask>();
	}
	
	public void shutdown() {
		synchronized (taskQueue) {
			int finishedCount = currentThreads;
			for( int i = 0; i < finishedCount; i++ ) {
				submitTask(FINISHED);
			}
			
			while ( currentThreads > 0 ) {
				try {
					taskQueue.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		
		// cleanup
		taskQueue    = null;
		rt           = null;
		eventManager = null;
	}
	
	protected ImplicitBlockingTask getWork() {
		synchronized (taskQueue) {
			if ( taskQueue.isEmpty() ) {
				ImplicitBlockingTask task = null;
				while ( task == null ) {
					try {
						sleepingThreads++;
						eventManager.signalThreadSuspend(Thread.currentThread());
						taskQueue.wait();
						if ( !taskQueue.isEmpty() ) {
							task = taskQueue.remove(0);
						}
					} catch (InterruptedException e) {
						// wait more
					} finally {
						sleepingThreads--;
					}
				}
				return task;
			} else { 
				return taskQueue.remove(0);
			}
		}
	}
	
	protected void singalThreadFinished() {
		synchronized (taskQueue) {
			currentThreads--;
			if ( currentThreads == 0 ) {
				taskQueue.notifyAll();
			}
		}
	}
	
	public final void submitTask(ImplicitBlockingTask task) {
		synchronized (taskQueue) {
			taskQueue.add(task);
			if ( sleepingThreads > 0 ) {
				taskQueue.notify();
			} else {
				if ( currentThreads < maxThreads ) {
					// create new thread
					new BlockingThread().start();
					currentThreads++;
				}
			}
		}
	}
	
	protected final class BlockingThread extends AeminiumThread {
		protected boolean finished = false;
		
		public BlockingThread() {
			setName("BlockingThead-" + currentThreads);
		}
		
		@Override
		public void run() {			
			eventManager.signalNewThread(Thread.currentThread());
			while ( !finished ) {
				ImplicitBlockingTask task = getWork();
				if ( task != FINISHED ) {
					task.invoke(rt);
					task = null;
				} else {
					finished = true;
				}
			}
			singalThreadFinished();
		}
	}
}
