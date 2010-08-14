package aeminium.runtime.events;

import java.util.ArrayList;
import java.util.Collection;

public class EventManager implements RuntimeEventManager  {
	protected Collection<RuntimeEventListener> listeners;
	
	@Override
	public final void init() {
		listeners =  new ArrayList<RuntimeEventListener>();
	}

	@Override
	public final void shutdown() {
		listeners.clear();		
	}
	
	@Override
	public final void registerRuntimeEventListener(RuntimeEventListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
		
	}

	@Override
	public final void signalPolling() {
		synchronized (listeners) {
			for (RuntimeEventListener listener : listeners) {
				listener.onPolling();
			}
		}
	}

	@Override
	public final void signalThreadSuspend(Thread thread) {
		synchronized (listeners) {
			for (RuntimeEventListener listener : listeners) {
				listener.onThreadSuspend(thread);
			}
		}
	}

	@Override
	public final void signalNewThread(Thread thread) {
		synchronized (listeners) {
			for (RuntimeEventListener listener : listeners) {
				listener.onNewThread(thread);
			}
		}
	}
	
}
