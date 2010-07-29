package aeminium.runtime.prioritizer.reverse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.AbstractPrioritizer;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public class ReversePrioritizer<T extends RuntimeTask> extends AbstractPrioritizer<T> {
	private final RuntimeTask[] rta = new RuntimeTask[0];
	
	
	public ReversePrioritizer(RuntimeScheduler<T> scheduler, EnumSet<Flags> flags) {
		super(scheduler, flags);
	}

	@Override
	public void init() {
	}

	@Override
	public void scheduleTasks(Collection<T> tasks) {
		List<T> newOrder = new ArrayList<T>(tasks);
		Collections.reverse(newOrder);
		scheduler.scheduleTasks(newOrder);
	}

	@Override
	public void scheduleTask(T task) {
		scheduler.scheduleTask(task);		
	}

	
	@Override
	public void shutdown() {
	}


}
