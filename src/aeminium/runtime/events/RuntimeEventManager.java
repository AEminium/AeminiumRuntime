package aeminium.runtime.events;


public interface RuntimeEventManager {
	public void init();
	public void shutdown();
	public void registerRuntimeEventListener(RuntimeEventListener listener);
	public void signalPolling();
	public void signalThreadSuspend(Thread thread);
	public void signalNewThread(Thread thread);
}
