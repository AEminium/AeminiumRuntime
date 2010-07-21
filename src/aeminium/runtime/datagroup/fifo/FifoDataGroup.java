package aeminium.runtime.datagroup.fifo;

import java.util.LinkedList;
import java.util.List;

import aeminium.runtime.DataGroup;
import aeminium.runtime.datagroup.DataGroupFactory;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public class FifoDataGroup<T extends RuntimeTask> implements RuntimeDataGroup<T> {	
	private boolean locked = false;
	private List<T> waitQueue = new LinkedList<T>();
	private final RuntimeScheduler<T> scheduler;

	protected FifoDataGroup(RuntimeScheduler<T> scheduler) {
		this.scheduler = scheduler;
	}

	@SuppressWarnings("unchecked")
	public static <T extends RuntimeTask> DataGroupFactory<T> createFactory(RuntimeScheduler<T> scheduler) {
		return (DataGroupFactory<T>) new DataGroupFactory<RuntimeTask>() {
			@Override 
			public void init() {}
			@Override 
			public void shutdown() {}
			@Override
			public DataGroup createDataGroup(RuntimeScheduler<RuntimeTask> scheduler) {
				return new FifoDataGroup<RuntimeTask>(scheduler);
			}
		};
	}
	
	@Override
	public boolean trylock(T task) {
		synchronized (this) {
			if ( locked ) {
				waitQueue.add(task);
				return false;
			} else {
				locked = true;
				return true;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void unlock() {
		synchronized (this) {
			locked = false;
			if (!waitQueue.isEmpty()) {
				T head = waitQueue.remove(0);
				scheduler.scheduleTasks(head);
			}
		}
	}
}