package aeminiumruntime.graphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import aeminiumruntime.Runtime;
import aeminiumruntime.RuntimeTask;
import aeminiumruntime.prioritizers.PrioritizableTaskGraph;
import aeminiumruntime.prioritizers.Prioritizer;
import aeminiumruntime.prioritizers.SmartPrioritizer;

public class ParallelTaskGraph implements TaskGraph, PrioritizableTaskGraph {

	/* Structures */

	HashMap<Integer, RuntimeTask> taskDictionary = new HashMap<Integer, RuntimeTask>();

	HashMap<Integer, Integer> taskChildrenCount = new HashMap<Integer, Integer>();
	HashMap<Integer, Integer> taskParent = new HashMap<Integer, Integer>();

	HashMap<Integer, List<Integer>> dependencies = new HashMap<Integer, List<Integer>>();
	HashMap<Integer, List<Integer>> reverseDependencies = new HashMap<Integer, List<Integer>>();

	List<Integer> readyQ = new ArrayList<Integer>();
	List<Integer> runningQ = new ArrayList<Integer>();
	List<Integer> holdingQ = new ArrayList<Integer>();
	List<Integer> doneQ = new ArrayList<Integer>();

	Prioritizer prioritizer;
	boolean debug;

	Logger lg = Logger.getLogger(ParallelTaskGraph.class.getName());

	public ParallelTaskGraph(Prioritizer p, boolean debug) {
		this.prioritizer = (p != null) ? p : new SmartPrioritizer(this);
		this.debug = debug;
	}

	public ParallelTaskGraph(Prioritizer p) {
		this(p, false);
	}

	public ParallelTaskGraph(boolean debug) {
		this(null, debug);
	}

	public ParallelTaskGraph() {
		this(null, false);
	}

	private synchronized void moveQueues(int key, List<Integer> from,
			List<Integer> to) {
		if (from != null) {
			from.remove(new Integer(key));
		}
		to.add(key);
	}

	@Override
	public synchronized boolean add(RuntimeTask task, RuntimeTask parent,
			Collection<RuntimeTask> deps) {

		synchronized (task) {
			int key = createKey(task);
			if (parent != null && parent != Runtime.NO_PARENT) {
				int pk = parent.getId();
				taskParent.put(key, pk);
				int count = taskChildrenCount.get(pk);
				taskChildrenCount.put(pk, count + 1);
			}

			boolean hasDeps = handleDeps(key, deps);

			if (!hasDeps) {
				moveQueues(key, null, readyQ);
			}

			task.getStatistics().setStartTime(System.nanoTime());
		}

		return false;
	}

	private void addOrAppend(HashMap<Integer, List<Integer>> map, int key,
			int value) {
		if (!map.containsKey(key)) {
			map.put(key, new ArrayList<Integer>());
		}
		map.get(key).add(value);
	}

	private boolean handleDeps(int key, Collection<RuntimeTask> deps) {
		boolean hasDeps = false;
		for (RuntimeTask t : deps) {
			int dk = createKey(t);
			if (!doneQ.contains(dk)) {
				addOrAppend(dependencies, key, dk);
				addOrAppend(reverseDependencies, dk, key);
				hasDeps = true;
			}
		}
		return hasDeps;
	}

	private int createKey(RuntimeTask task) {
		int key = task.getId();
		if (!taskDictionary.containsKey(key)) {
			taskDictionary.put(key, task);
			taskChildrenCount.put(key, 0);
		}
		return key;
	}

	@Override
	public boolean isDone() {
		return taskDictionary.isEmpty();
	}

	@Override
	public synchronized boolean hasNext() {
		updateGraph();
		return !readyQ.isEmpty();
	}

	private synchronized void updateGraph() {

		// Identify completed tasks
		List<Integer> tasksToBeRemoved = new ArrayList<Integer>();
		for (int key : runningQ) {
			RuntimeTask task = taskDictionary.get(key);
			synchronized (task) {
				if (task.isDone()) {
					tasksToBeRemoved.add(key);
				}
			}
		}

		// Act upon
		for (int key : tasksToBeRemoved) {
			synchronized (taskDictionary.get(key)) {
				moveQueues(key, runningQ, holdingQ);
				checkIfChildrenAreDone(key);
			}
		}
		
		if (this.isDone()) {
			this.notifyAll();
		}
		
		if (debug) {
			lg.log(Level.FINEST,"ready:" + readyQ);
			lg.log(Level.FINEST,"running:" + runningQ);
			lg.log(Level.FINEST,"holding:" + holdingQ);
			lg.log(Level.FINEST,"done:" + doneQ);
			lg.log(Level.FINEST,".........");
		}

	}

	private void checkIfChildrenAreDone(int key) {
		if (taskChildrenCount.get(key) == 0 && taskDictionary.get(key).isDone()) {
			moveQueues(key, holdingQ, doneQ);
			finishTask(key);
		}
	}

	private void finishTask(Integer key) {
		RuntimeTask task = taskDictionary.get(key);
		synchronized (task) {
			task.getStatistics().setOutTime(System.nanoTime());
			task.getStatistics().calcTime();

			// Releases dependent tasks
			if (reverseDependencies.containsKey(key)) {
				for (int dk : reverseDependencies.get(key)) {
					dependencies.get(dk).remove(key);
					checkTaskForRunning(dk);
				}
				reverseDependencies.remove(key);
			}

			// Releases parent.
			synchronized (taskParent) {
				if (taskParent.containsKey(key)) {
					int pk = taskParent.get(key);
					synchronized (taskDictionary.get(pk)) {
						int count = taskChildrenCount.get(pk);
						taskChildrenCount.put(pk, count - 1);
						checkIfChildrenAreDone(pk);
					}
				}
			}

			taskChildrenCount.remove(key);
			taskDictionary.remove(key);

		}
	}

	private void checkTaskForRunning(int key) {
		synchronized (taskDictionary.get(key)) {
			if (dependencies.get(key).isEmpty()) {
				dependencies.remove(new Integer(key));
				moveQueues(key, null, readyQ);
			}
		}
	}

	@Override
	public synchronized RuntimeTask next() {
		synchronized (readyQ) {
			if (readyQ.isEmpty()) {
				return null;
			}

			ArrayList<Integer> readyClone = new ArrayList<Integer>(readyQ);
			int key = prioritizer.getNext(readyClone);
			RuntimeTask task = taskDictionary.get(key);

			synchronized (task) {
				moveQueues(key, readyQ, runningQ);
			}
			task.getStatistics().setRunningTime(System.nanoTime());
			return task;
		}

	}

	@Override
	public void remove() {
	}

	@Override
	public <T> int countDependencies(T task) {
		// Returns tasks that depend on this one.
		if (reverseDependencies.containsKey(task)) {
			return reverseDependencies.get(task).size();
		} else {
			return 0;
		}
	}

	@Override
	public void checkForCycles(RuntimeTask t)
			throws DependencyDeadlockException {
		if (hasCycle(t.getId(), t.getId())) {
			throw new DependencyDeadlockException(t.getId());
		}
	}

	private boolean hasCycle(Integer where, Integer find) {
		if (dependencies.containsKey(where)) {
			for (Integer tid : dependencies.get(where)) {
				if (tid == find) {
					return true;
				}
				if (hasCycle(tid, find)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
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
