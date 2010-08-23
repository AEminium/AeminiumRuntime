package aeminium.runtime.implementations.implicitworkstealing.events;

import java.util.ArrayList;
import java.util.Collection;

public final class EventManager  {
	protected Collection<RuntimeEventListener> listeners;
	
	public final void init() {
		listeners =  new ArrayList<RuntimeEventListener>();
	}

	public final void shutdown() {
		listeners.clear();		
	}
	
	public final void registerRuntimeEventListener(RuntimeEventListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
		
	}

	public final void signalPolling() {
		synchronized (listeners) {
			for (RuntimeEventListener listener : listeners) {
				listener.onPolling();
			}
		}
	}

	public final void signalThreadSuspend(Thread thread) {
		synchronized (listeners) {
			for (RuntimeEventListener listener : listeners) {
				listener.onThreadSuspend(thread);
			}
		}
	}

	public final void signalNewThread(Thread thread) {
		synchronized (listeners) {
			for (RuntimeEventListener listener : listeners) {
				listener.onNewThread(thread);
			}
		}
	}
	
}
