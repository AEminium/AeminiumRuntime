package aeminium.runtime.implementations.implicitworkstealing.events;

public interface RuntimeEventListener {
	public void onPolling();
	public void onThreadSuspend(Thread thread);
	public void onNewThread(Thread thread);
}
