package aeminium.runtime.prioritizer.lowestlevelfirst;

import java.util.Comparator;
import java.util.PriorityQueue;

import aeminium.runtime.implementations.implicitworkstealing.events.RuntimeEventManager;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;
import aeminium.runtime.prioritizer.AbstractPrioritizer;
import aeminium.runtime.scheduler.RuntimeScheduler;

@SuppressWarnings("unchecked")
public class LowestLevelFirstPrioritizer<T extends ImplicitTask> extends AbstractPrioritizer<T> {
	protected PriorityQueue<T> waitingQueue = null;
	
	public LowestLevelFirstPrioritizer(RuntimeScheduler<T> scheduler) {
		super(scheduler);
	}

	@Override
	public void init(RuntimeEventManager eventManager) {
		waitingQueue = new PriorityQueue<T>(20, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return o2.level - o1.level;
			}
		});
	}

	@Override
	public void shutdown() {
	}
	
	@Override
	public  final void scheduleTask(T task) {
		synchronized (this) {
			waitingQueue.add(task);
			schedule();
		}
	}
	
	protected  final void schedule() {
		synchronized (this) {
			int count  = scheduler.getMaxParallelism() - scheduler.getRunningTasks();

			if ( count > 0 && !waitingQueue.isEmpty() ) {
				count = Math.min(count, waitingQueue.size());

				if ( count == 1 ) {
					scheduler.scheduleTask(waitingQueue.remove());
				} else {
					for ( int i = 0; i < count; i++ ) {
						scheduler.scheduleTask(waitingQueue.remove());
					}
				}
			}
		}
	}

	@Override
	public final void taskFinished(T task) {
		schedule();
	}
	
	public final void taskPaused(T task) {
		schedule();
	}

}
