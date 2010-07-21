package aeminiumruntime.datagroup;

import aeminiumruntime.DataGroup;
import aeminiumruntime.task.RuntimeTask;

public interface RuntimeDataGroup<T extends RuntimeTask> extends DataGroup {
	public boolean trylock(T task);
	public void unlock();
}
