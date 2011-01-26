package aeminium.runtime.implementations.implicitworkstealing.scheduler;

public abstract class AeminiumThread extends Thread {
	public volatile int taskCount;
}
