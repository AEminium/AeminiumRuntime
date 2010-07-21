package aeminiumruntime.task;

import aeminiumruntime.AtomicTask;
import aeminiumruntime.datagroup.RuntimeDataGroup;

public interface RuntimeAtomicTask<T extends RuntimeTask> extends AtomicTask, RuntimeTask {
	public RuntimeDataGroup<T> getDataGroup();
}
