package aeminium.runtime.prioritizer.blockingfirst;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;

import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.AbstractPrioritizer;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public class BlockingFirstPrioritizer<T extends RuntimeTask> extends AbstractPrioritizer<T> {

	public BlockingFirstPrioritizer(RuntimeScheduler<T> scheduler, EnumSet<Flags> flags) {
		super(scheduler, flags);
	}

	@Override
	public void init() {
	}

	@Override
	public void scheduleTasks(T... tasks) {
		Arrays.sort(tasks, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				if ( o1 instanceof NonBlockingTask ) {
					if ( o2 instanceof NonBlockingTask ) {
						return 0;
					} else {
						return 1;
					}
				} else {
					// o1 blocking task
					if ( o2 instanceof NonBlockingTask ) {
						return -1;
					} else {
						return 0;
					}
				}
			}
		});
		scheduler.scheduleTasks(tasks);
	}

	@Override
	public void shutdown() {
	}

}
