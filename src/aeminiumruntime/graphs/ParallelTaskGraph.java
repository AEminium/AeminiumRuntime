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

	HashMap<Integer, RuntimeTask> taskDictionary = new HashMap<Integer, RuntimeTask>();

	HashMap<Integer, List<Integer>> taskDependencies = new HashMap<Integer, List<Integer>>();
	HashMap<Integer, List<Integer>> dependentTasks = new HashMap<Integer, List<Integer>>();
	
	HashMap<Integer, List<Integer>> childTasks = new HashMap<Integer, List<Integer>>();
	HashMap<Integer, Integer> parentTasks = new HashMap<Integer, Integer>();

	
	List<Integer> readyQ = new ArrayList<Integer>();
	List<Integer> runningQ = new ArrayList<Integer>();
	List<Integer> holdingQ = new ArrayList<Integer>();
	List<Integer> doneQ = new ArrayList<Integer>();

	Prioritizer prioritizer;
	boolean debug;

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

	@Override
	public boolean add(RuntimeTask task, RuntimeTask parent, Collection<RuntimeTask> deps) {

		int tid = task.getId();
		addIfNotInDictionary(task);
		
		if (parent != null && parent != Runtime.NO_PARENT) {
			addIfNotInDictionary(parent);
			addOrAppend(parent.getId(), task.getId(), childTasks);
			parentTasks.put(task.getId(), parent.getId());
		}
		
		if (!taskDependencies.containsKey(tid)) {
			List<Integer> depIds = new ArrayList<Integer>();
			for (RuntimeTask t : deps) {
				int dtid = addIfNotInDictionary(t);
				if (!doneQ.contains(dtid)) {
					depIds.add(dtid);
				}
				addOrAppend(dtid, task.getId(), dependentTasks);
			}
			
			if (depIds.isEmpty()) {
				appendToReady(task.getId());
			} else {
				taskDependencies.put(task.getId(), depIds);
			}
		}
		
		task.getStatistics().setStartTime(System.nanoTime());
		return true;
	}
	
	private int addIfNotInDictionary(RuntimeTask task) {
		int tid = task.getId();
		if (!taskDictionary.containsKey(tid)) {
			taskDictionary.put(tid, task);
		}
		return tid;
	}

	private void appendToReady(int tid) {
		if (!runningQ.contains(tid)) {
			taskDependencies.remove(new Integer(tid));
			taskDictionary.get(tid).getStatistics().setReadyTime(System.nanoTime());
			readyQ.add(tid);
		}
	}

	private void addOrAppend(int key, int value,
			HashMap<Integer, List<Integer>> store) {
		if (!store.containsKey(key)) {
			store.put(key, new ArrayList<Integer>());
		}
		store.get(key).add(value);
	}

	@Override
	public boolean isDone() {
		return taskDictionary.isEmpty();
	}

	@Override
	public boolean hasNext() {
		updateGraph();
		return !readyQ.isEmpty();
	}

	@Override
	public RuntimeTask next() {
		if (readyQ.isEmpty()) {
			return null;
		}
		int tid = prioritizer.getNext(readyQ);

		readyQ.remove(new Integer(tid));
		runningQ.add(tid);

		RuntimeTask task = taskDictionary.get(tid);
		task.getStatistics().setRunningTime(System.nanoTime());
		return task;
	}

	protected synchronized void updateGraph() {
		List<Integer> tasksToBeRemoved = new ArrayList<Integer>();
		for (int tid : runningQ) {
			if (taskDictionary.get(tid).isDone()) {
				tasksToBeRemoved.add(tid);
			}

		}
		
		for (Integer tid : tasksToBeRemoved) {
			runningQ.remove(tid);
			if (hasChildrenAlive(tid)) {
				holdingQ.add(tid);
			} else {
				finishTask(tid);
				doneQ.add(tid);
			}
		}
		if (debug) {
			Logger lg = Logger.getLogger(ParallelTaskGraph.class.getName());
            lg.log(Level.INFO, "Ready:" + readyQ);
            lg.log(Level.INFO, "Running:" + runningQ);
            lg.log(Level.INFO, "Holding:" + holdingQ);
            lg.log(Level.INFO, "Done:" + doneQ);
            lg.log(Level.INFO, "Dependencies:" + taskDependencies);	
		}
	}
	
	private boolean hasChildrenAlive(int tid) {
		return childTasks.containsKey(tid);
	}

	private void finishTask(int tid) {
		taskDictionary.get(tid).getStatistics().setOutTime(
				System.nanoTime());
		taskDictionary.get(tid).getStatistics().calcTime();
		
		if (dependentTasks.containsKey(tid)) {
			for (int dId : dependentTasks.get(tid)) {
				taskDependencies.get(dId).remove(new Integer(tid));
				checkTaskForRunning(dId);
			}
			dependentTasks.remove(tid);
		}
		
		if (parentTasks.containsKey(tid)) {
			int pid = parentTasks.get(tid);
			parentTasks.remove(tid);
			childTasks.get(pid).remove(tid);
			if ( childTasks.get(pid).isEmpty() ) {
				childTasks.remove(pid);
				holdingQ.remove(new Integer(pid));
				doneQ.add(pid);
				finishTask(pid);
			}
		}
		
		if (!this.debug) {
			taskDictionary.remove(tid);
		}
	}

	private void checkTaskForRunning(int tid) {
		if (taskDependencies.get(tid).isEmpty()) {
			taskDependencies.remove(tid);
			appendToReady(tid);
		}
	}

	@Override
	public void remove() {
	}

	@Override
	public <T> int countDependencies(T tid) {
		if (dependentTasks.containsKey(tid)) {
			return dependentTasks.get(tid).size();
		} else {
			return 0;
		}
	}

	public void checkForCycles(RuntimeTask t)
			throws DependencyDeadlockException {
		if (hasCycle(t.getId(), t.getId())) {
			throw new DependencyDeadlockException(t.getId());
		}
	}

	private boolean hasCycle(Integer where, Integer find) {
		if (taskDependencies.containsKey(where)) {
			for (Integer tid : taskDependencies.get(where)) {
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

}
