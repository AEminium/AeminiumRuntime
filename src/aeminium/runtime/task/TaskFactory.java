package aeminium.runtime.task;

import aeminium.runtime.AtomicTask;
import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.datagroup.RuntimeDataGroup;

public interface TaskFactory<T extends RuntimeTask> {
	public void init();
	public void shutdown();
	public BlockingTask createBlockingTask(Body body, short hints);
	public NonBlockingTask createNonBlockingTask(Body body, short hints);
	public AtomicTask createAtomicTask(Body body, RuntimeDataGroup<T> datagroup, short hints);
}
