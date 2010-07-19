package aeminiumruntime.graphs;

import java.util.ArrayList;
import java.util.List;

import aeminiumruntime.RuntimeTask;

public abstract class BaseTaskGraph implements TaskGraph {

	List<RuntimeTask> readyList = new ArrayList<RuntimeTask>();
	List<RuntimeTask> runningList = new ArrayList<RuntimeTask>();

	protected abstract void updateGraph();

	@Override
	public synchronized boolean hasNext() {
		updateGraph();
		this.notifyAll();
		return !readyList.isEmpty();
	}

	public synchronized void waitForAllTasks() {
		while (!isDone()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
