package aeminium.runtime.events;

public interface RuntimeEventManager {
	public void registerRuntimeEventListener(RuntimeEventListener listener);
	public void signalPolling();
	public void signalThreadSuspend(Thread thread);
}
