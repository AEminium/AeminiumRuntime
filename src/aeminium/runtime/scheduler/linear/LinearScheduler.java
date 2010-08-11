package aeminium.runtime.scheduler.linear;

import java.util.Collection;

import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;

public class LinearScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {

	public LinearScheduler() {
		super();
	}

	@Override
	public final void init(RuntimeEventManager eventManager) {
	}
	
	@Override
	public final void shutdown() {
	}

	@Override
	public final void scheduleTasks(Collection<T> tasks) {
		for ( T t : tasks ) {
			try {
				t.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public final void scheduleTask(T task) {
		try {
			task.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
