package aeminium.runtime.taskcounter;

import java.util.ArrayList;
import java.util.List;

import aeminium.runtime.implementations.Configuration;


public class TaskCounter implements RuntimeTaskCounter {
	protected List<Thread> threads = new ArrayList<Thread>(2*Configuration.getProcessorCount());
	protected boolean polling = false;
	protected State state = State.INITIALIZED;
	protected long masterDelta = 0;
	
	enum State {
		INITIALIZED,
		WAITING_TO_COMPLETE,
	}
	
	public TaskCounter() {
	}

	@Override
	public void setPolling() {
		synchronized (this) {
			polling = true;
		}
	}
	
	@Override
	public <T extends Thread & TaskCountingThread> void registerThread(T thread) {
		synchronized (this) {
			threads.add(thread);
		}		
	}

	@Override
	public <T extends Thread & TaskCountingThread> void unregisterThread(T thread) {
		synchronized (this) {
			threads.remove(thread);
		}		
	}

	@Override
	public <T extends Thread & TaskCountingThread> void threadWaiting (T thread) {
		synchronized (this) {
			if ( checkCompletion(thread)) {
				this.notifyAll();
			}
		}
	}
	
	@Override
	public void waitToEmpty(long delta) {
		Thread master = Thread.currentThread();
		synchronized (this) {
			state = State.WAITING_TO_COMPLETE;
			masterDelta = delta;
			while ( !checkCompletion(master) ) {
				try {
					if ( polling ) {
						this.wait(100);
					} else {
						this.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected boolean checkCompletion(Thread current) {
		synchronized (this) {
			if ( state == State.WAITING_TO_COMPLETE ) {
				long counter = masterDelta;
				for (Thread t : threads) {
					counter += ((TaskCountingThread)t).getDelta();
				}
				return (counter == 0);
			} else {
				return false;
			}
		}
	}
}
