package aeminiumruntime.datagroup;

import aeminiumruntime.DataGroup;
import aeminiumruntime.scheduler.RuntimeScheduler;
import aeminiumruntime.task.RuntimeTask;

public interface DataGroupFactory<T extends RuntimeTask> {
	public DataGroup createDataGroup(RuntimeScheduler<T> scheduler);
}
