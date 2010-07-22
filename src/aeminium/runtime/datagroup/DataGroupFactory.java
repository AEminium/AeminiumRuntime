package aeminium.runtime.datagroup;

import aeminium.runtime.DataGroup;
import aeminium.runtime.task.RuntimeTask;

public interface DataGroupFactory<T extends RuntimeTask> {
	public void init();
	public void shutdown();
	public DataGroup createDataGroup();
}
