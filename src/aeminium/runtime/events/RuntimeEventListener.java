package aeminium.runtime.events;

public interface RuntimeEventListener {
	public void onPolling();
	public void onThreadSuspend(Thread thread);
	public void onNewThread(Thread thread);
}
