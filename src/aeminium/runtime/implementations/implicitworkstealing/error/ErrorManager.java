package aeminium.runtime.implementations.implicitworkstealing.error;

import aeminium.runtime.ErrorHandler;
import aeminium.runtime.Task;

public interface ErrorManager {
	/* manage subscritpions */
	public void addErrorHandler(final ErrorHandler eh);
	public void removeErrorHandler(final ErrorHandler eh);
	
	/* singal errors */
	public void signalTaskException(final Task task, final Throwable  e);
	public void singalDependencyCycle(final Task task);
	public void singalTaskDuplicatedSchedule(final Task task);
	public void singalInternalError(final Error err);
	public void signalLockingDeadlock();
}
