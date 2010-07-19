package aeminiumruntime.queue;

public interface QScheduler {
	public void schedule(QAbstractTask task);
	public void shutdown();
}
