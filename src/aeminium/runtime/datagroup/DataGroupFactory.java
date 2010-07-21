package aeminium.runtime.datagroup;

import aeminium.runtime.DataGroup;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public interface DataGroupFactory<T extends RuntimeTask> {
	public DataGroup createDataGroup(RuntimeScheduler<T> scheduler);
}
