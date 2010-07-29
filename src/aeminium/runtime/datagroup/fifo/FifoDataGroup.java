package aeminium.runtime.datagroup.fifo;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.DataGroup;
import aeminium.runtime.datagroup.AbstractDataGroup;
import aeminium.runtime.datagroup.AbstractDataGroupFactory;
import aeminium.runtime.datagroup.DataGroupFactory;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public class FifoDataGroup<T extends RuntimeTask> extends AbstractDataGroup<T> {	
	private boolean locked = false;
	private List<T> waitQueue = new LinkedList<T>();
	private T owner = null;
	
	protected FifoDataGroup(RuntimeScheduler<T> scheduler, EnumSet<Flags> flags) {
		super(scheduler, flags);
	}

	@SuppressWarnings("unchecked")
	public static <T extends RuntimeTask> DataGroupFactory<T> createFactory(RuntimeScheduler<T> scheduler, EnumSet<Flags> flags) {
		return (DataGroupFactory<T>) new AbstractDataGroupFactory<T>(scheduler, flags) {
			@Override 
			public void init() {}
			@Override 
			public void shutdown() {}
			@Override
			public DataGroup createDataGroup() {
				return new FifoDataGroup<T>(scheduler, flags);
			}
		};
	}
	
	@Override
	public boolean trylock(T task) {
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

	@SuppressWarnings("unchecked")
	public void unlock() {
		synchronized (this) {
			locked = false;
			owner = null;
			if (!waitQueue.isEmpty()) {
				T head = waitQueue.remove(0);
				scheduler.taskResume(head);
			}
		}
	}

	public String toString() {
		if ( locked == false ) {
			return "DataGroup[UNLOCKED]";
		} else {
			return "DataGroup[LOCKED"+owner+"]";
		}
	}

}