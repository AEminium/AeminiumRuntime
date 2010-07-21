package aeminiumruntime.scheduler.linear;

import java.util.EnumSet;

import aeminiumruntime.implementations.Flag;
import aeminiumruntime.scheduler.AbstractScheduler;
import aeminiumruntime.task.RuntimeTask;

public class LinearScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {

	public LinearScheduler(EnumSet<Flag> flags) {
		super(flags);
	}

	@Override
	public void scheduleTasks(T... tasks) {
		for ( int i = 0 ; i < tasks.length ; i++ ) {
			try {
				tasks[i].call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void shutdown() {
	}

}
