package aeminium.runtime.datagroup.random;

import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.DataGroup;
import aeminium.runtime.datagroup.AbstractDataGroup;
import aeminium.runtime.datagroup.AbstractDataGroupFactory;
import aeminium.runtime.datagroup.DataGroupFactory;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public class RandomDataGroup<T extends RuntimeTask> extends AbstractDataGroup<T> {	
	private boolean locked = false;
	private List<T> waitQueue = new LinkedList<T>();
	private T owner = null;
	
	protected RandomDataGroup(RuntimeScheduler<T> scheduler) {
		super(scheduler);
	}

	public final static <T extends RuntimeTask> DataGroupFactory<T> createFactory(RuntimeScheduler<T> scheduler) {
		return (DataGroupFactory<T>) new AbstractDataGroupFactory<T>(scheduler) {
			@Override 
			public void init() {}
			@Override 
			public void shutdown() {}
			@Override
			public final DataGroup createDataGroup() {
				return new RandomDataGroup<T>(scheduler);
			}
		};
	}
	
	@Override
	public final boolean trylock(T task) {
		synchronized (this) {
			if ( locked ) {
				waitQueue.add(task);
				scheduler.taskPaused(task);
				return false;
			} else {
				locked = true;
				owner = task;
				return true;
			}
		}
	}

	public final void unlock() {
		synchronized (this) {
			locked = false;
			owner = null;
			if (!waitQueue.isEmpty()) {
				T head = waitQueue.remove((int)(Math.random()*(waitQueue.size()-1)));
				scheduler.taskResume(head);
			}
		}
	}

	public final String toString() {
		if ( locked == false ) {
			return "DataGroup[UNLOCKED]";
		} else {
			return "DataGroup[LOCKED"+owner+"]";
		}
	}

}