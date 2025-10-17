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

package aeminium.runtime.implementations.implicitworkstealing.events;

import java.util.ArrayList;
import java.util.Collection;

public final class EventManager  {
	protected Collection<RuntimeEventListener> listeners;

	public final void init() {
		listeners =  new ArrayList<RuntimeEventListener>();
	}

	public final void shutdown() {
		listeners.clear();
	}

	public final void registerRuntimeEventListener(RuntimeEventListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}

	}

	public final void signalPolling() {
		synchronized (listeners) {
			for (RuntimeEventListener listener : listeners) {
				listener.onPolling();
			}
		}
	}

	public final void signalThreadSuspend(Thread thread) {
		synchronized (listeners) {
			for (RuntimeEventListener listener : listeners) {
				listener.onThreadSuspend(thread);
			}
		}
	}

	public final void signalNewThread(Thread thread) {
		synchronized (listeners) {
			for (RuntimeEventListener listener : listeners) {
				listener.onNewThread(thread);
			}
		}
	}

}
