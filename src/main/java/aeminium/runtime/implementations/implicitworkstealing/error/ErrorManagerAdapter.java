/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 *
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	public void signalDependencyCycle(final Task task) {
		synchronized (handlers) {
			for (ErrorHandler eh : handlers) {
				eh.handleDependencyCycle(task);
			}
		}
	}

	@Override
	public void signalTaskDuplicatedSchedule(final Task task) {
		synchronized (handlers) {
			for (ErrorHandler eh : handlers) {
				eh.handleTaskDuplicatedSchedule(task);
			}
		}
	}

	@Override
	public void signalInternalError(final Error err) {
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
