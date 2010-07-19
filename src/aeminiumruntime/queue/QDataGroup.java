package aeminiumruntime.queue;

import java.util.LinkedList;
import java.util.List;

import aeminiumruntime.DataGroup;

public class QDataGroup implements DataGroup {
	private boolean locked = false;
	private List<QAbstractTask> waitQueue = new LinkedList<QAbstractTask>();
	private QScheduler scheduler;
	
	public QDataGroup(QScheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public boolean trylock(QAbstractTask task) {
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
	
	public void unlock() {
		synchronized (this) {
			locked = false;
			if (!waitQueue.isEmpty()) {
				QAbstractTask head = waitQueue.remove(0);
				scheduler.schedule(head);
			}
		}
	}
}

