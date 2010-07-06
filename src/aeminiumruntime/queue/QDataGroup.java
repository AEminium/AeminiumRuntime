package aeminiumruntime.queue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.crypto.Data;

import aeminiumruntime.DataGroup;

public class QDataGroup implements DataGroup {
	private Lock lock = new ReentrantLock();
	private List<QAbstractTask> waitQueue = new LinkedList<QAbstractTask>();
	private QScheduler scheduler;
	
	public QDataGroup(QScheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public boolean trylock(QAbstractTask task) {
		synchronized (this) {
			boolean locked = lock.tryLock();			
			if ( !locked ) {
				waitQueue.add(task);
			}
			return locked;
		}
	}
	
	public void unlock() {
		synchronized (this) {
			lock.unlock();
			if (!waitQueue.isEmpty()) {
				QAbstractTask head = waitQueue.remove(0);
				scheduler.schedule(head);
			}
		}
	}
}
