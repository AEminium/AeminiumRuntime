package aeminium.runtime.implementations.implicitworkstealing.error;

import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.ErrorHandler;
import aeminium.runtime.Task;

public final class ErrorManagerAdapter implements ErrorManager {
	protected List<ErrorHandler> handlers = new LinkedList<ErrorHandler>();
	
	public void addErrorHandler(final ErrorHandler eh) {
		synchronized (handlers) {
			if (eh != null) {
				handlers.add(eh);
			}
		}
	}
	
	public void removeErrorHandler(final ErrorHandler eh) {
		synchronized (handlers) {
			if (eh != null) {
				handlers.remove(eh);
			}
		}
	}

	@Override
	public void signalTaskException(final Task task, final Throwable e) {
		synchronized (handlers) {
			for (ErrorHandler eh : handlers) {
				eh.handleTaskException(task, e);
			}
		}
	}

	@Override
	public void singalDependencyCycle(final Task task) {
		synchronized (handlers) {
			for (ErrorHandler eh : handlers) {
				eh.handleDependencyCycle(task);
			}
		}		
	}
	
	@Override
	public void singalTaskDuplicatedSchedule(final Task task) {
		synchronized (handlers) {
			for (ErrorHandler eh : handlers) {
				eh.handleTaskDuplicatedSchedule(task);
			}
		}
	}

	@Override
	public void singalInternalError(final Error err) {
		synchronized (handlers) {
			for (ErrorHandler eh : handlers) {
				eh.handleInternalError(err);
			}
		}
	}

	@Override
	public void signalLockingDeadlock() {
		synchronized (handlers) {
			for (ErrorHandler eh : handlers) {
				eh.handleLockingDeadlock();
			}
		}
	}

}
