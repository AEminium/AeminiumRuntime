package aeminium.runtime.datagroup;

import aeminium.runtime.DataGroup;
import aeminium.runtime.task.RuntimeTask;

public interface RuntimeDataGroup<T extends RuntimeTask> extends DataGroup {
	public boolean trylock(T task);
	public void unlock();
}
