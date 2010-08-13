package aeminium.runtime.task;

import aeminium.runtime.AtomicTask;
import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.datagroup.RuntimeDataGroup;

public interface TaskFactory<T extends RuntimeTask> {
	public void init();
	public void shutdown();
	public BlockingTask createBlockingTask(Body body, long hints);
	public NonBlockingTask createNonBlockingTask(Body body, long hints);
	public AtomicTask createAtomicTask(Body body, RuntimeDataGroup<T> datagroup, long hints);
}
