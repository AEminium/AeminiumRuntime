package aeminium.runtime.scheduler.linear;

import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.task.RuntimeTask;

public class LinearScheduler<T extends RuntimeTask> extends AbstractScheduler<T> {

	public LinearScheduler(EnumSet<Flags> flags) {
		super(flags);
	}

	@Override
	public void init() {
		
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
