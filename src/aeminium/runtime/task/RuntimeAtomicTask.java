package aeminium.runtime.task;

import aeminium.runtime.AtomicTask;
import aeminium.runtime.datagroup.RuntimeDataGroup;

public interface RuntimeAtomicTask<T extends RuntimeTask> extends AtomicTask {
	public RuntimeDataGroup<T> getDataGroup();
}
