package aeminium.runtime.taskcounter;

public interface RuntimeTaskCounter {
	public <T extends Thread & TaskCountingThread> void registerThread(T thread);
	public <T extends Thread & TaskCountingThread> void unregisterThread(T thread);
	public <T extends Thread & TaskCountingThread> void threadWaiting(T thread);
	public void setPolling();
	public void waitToEmpty(long delta);
}
