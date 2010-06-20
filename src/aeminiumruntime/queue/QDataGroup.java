package aeminiumruntime.queue;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import aeminiumruntime.DataGroup;

public class QDataGroup implements DataGroup {
	private Lock lock = new ReentrantLock();
	
	public void lock() {
		lock.lock();
	}
	
	public void unlock() {
		lock.unlock();
	}
}
