package aeminium.runtime.taskcounter;

public interface TaskCountingThread {
	public void tasksAdded(int delta);
	public void tasksCompleted(int delta);
	public long getDelta();
}
