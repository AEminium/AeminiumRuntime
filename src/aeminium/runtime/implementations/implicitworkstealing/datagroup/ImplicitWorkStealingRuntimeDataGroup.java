package aeminium.runtime.implementations.implicitworkstealing.datagroup;

import aeminium.runtime.DataGroup;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public interface ImplicitWorkStealingRuntimeDataGroup extends DataGroup {
	public boolean trylock(ImplicitWorkStealingRuntime rt, ImplicitTask task);
	public void unlock(ImplicitWorkStealingRuntime rt, ImplicitTask task);
}
