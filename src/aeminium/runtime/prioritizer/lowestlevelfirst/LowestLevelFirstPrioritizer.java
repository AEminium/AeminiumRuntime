package aeminium.runtime.prioritizer.lowestlevelfirst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.PriorityQueue;

import aeminium.runtime.RuntimeError;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.AbstractPrioritizer;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.implicit2.ImplicitTask2;

public class LowestLevelFirstPrioritizer<T extends ImplicitTask2> extends AbstractPrioritizer<T> {
	protected PriorityQueue<T> waitingQueue = null;
	
	public LowestLevelFirstPrioritizer(RuntimeScheduler<T> scheduler, EnumSet<Flags> flags) {
		super(scheduler, flags);
		scheduler.setPrioritizer(this);
	}

	@Override
	public void init() {
		waitingQueue = new PriorityQueue<T>(20, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return o2.getLevel() - o1.getLevel();
			}
		});
	}

	@Override
	public void shutdown() {
	}
	
	@Override
	public void scheduleTasks(Collection<T> tasks) {
		synchronized (this) {
			waitingQueue.addAll(tasks);
			schedule();
		}
	}

	@Override
	public void scheduleTask(T task) {
		synchronized (this) {
			waitingQueue.add(task);
			schedule();
		}
	}
	
	protected void schedule() {
		synchronized (this) {
			int count  = scheduler.getMaxParallelism() - scheduler.getRunningTasks();

			if ( count > 0 && !waitingQueue.isEmpty() ) {
				count = Math.min(count, waitingQueue.size());

				if ( count == 1 ) {
					scheduler.scheduleTask(waitingQueue.remove());
				} else {
					List<T> tasks = new ArrayList<T>(count);
					for ( int i = 0; i < count; i++ ) {
						tasks.add(waitingQueue.remove());
					}
					scheduler.scheduleTasks(tasks);
				}
			}
		}
	}

	@Override
	public void taskFinished(T task) {
		schedule();
	}
	
	public void taskPaused(T task) {
		schedule();
	}

}
