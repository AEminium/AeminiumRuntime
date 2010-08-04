package aeminium.runtime.task;

import java.util.Collection;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.datagroup.RuntimeDataGroup;

public interface TaskFactory<T extends RuntimeTask> {
	public void init();
	public void shutdown();
	public BlockingTask createBockingTask(Body body, Collection<Hints> hints);
	public NonBlockingTask createNonBockingTask(Body body, Collection<Hints> hints);
	public RuntimeAtomicTask<T> createAtomicTask(Body body, RuntimeDataGroup<T> datagroup, Collection<Hints> hints);
}
