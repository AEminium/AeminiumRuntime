package aeminiumruntime.task;

import java.util.Collection;

import aeminiumruntime.AtomicTask;
import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.NonBlockingTask;
import aeminiumruntime.datagroup.RuntimeDataGroup;

public interface TaskFactory<T extends RuntimeTask> {
	public BlockingTask createBockingTask(Body body, Collection<Hint> hints);
	public NonBlockingTask createNonBockingTask(Body body, Collection<Hint> hints);
	public RuntimeAtomicTask<T> createAtomicTask(Body body, RuntimeDataGroup<T> datagroup, Collection<Hint> hints);
}
