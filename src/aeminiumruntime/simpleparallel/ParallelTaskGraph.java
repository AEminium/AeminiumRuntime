package aeminiumruntime.simpleparallel;

import aeminiumruntime.RuntimeTask;
import aeminiumruntime.TaskGraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ParallelTaskGraph implements TaskGraph {
    HashMap<Integer, RuntimeTask> taskDictionary = new HashMap<Integer, RuntimeTask>();
    HashMap<Integer, List<Integer>> taskDependencies = new HashMap<Integer, List<Integer>>();
    HashMap<Integer, List<Integer>> taskChildren = new HashMap<Integer, List<Integer>>();
    List<RuntimeTask> readyList = new ArrayList<RuntimeTask>();
    List<RuntimeTask> runningList = new ArrayList<RuntimeTask>();

    public synchronized boolean add(RuntimeTask task, Collection<RuntimeTask> deps) {
        task.getStatistics().setStartTime(System.nanoTime());
        taskDictionary.put(task.getId(), task);
        List<Integer> depIds = new ArrayList<Integer>();
        for (RuntimeTask t : deps) {
            depIds.add(t.getId());
            addOrAppendChild(t.getId(), task.getId());
        }
        taskDependencies.put(task.getId(), depIds);
        if (deps.isEmpty()) {
            appendToReady(task);
        }
        return true;
    }

    private void appendToReady(RuntimeTask task) {
        if (!runningList.contains(task)) {
            task.getStatistics().setReadyTime(System.nanoTime());
            readyList.add(task);
        }
    }

    private void addOrAppendChild(int dep, int task) {
        if (!taskChildren.containsKey(dep)) {
            taskChildren.put(dep, new ArrayList<Integer>());
        }
        taskChildren.get(dep).add(task);
    }

    /* Removes finished tasks from dependencies  */
    private synchronized void updateGraph() {
        List<Integer> tasksToBeRemoved = new ArrayList<Integer>();
        for (RuntimeTask task : runningList) {
            if (task.isDone()) {
                int tid = task.getId();
                if ( taskChildren.containsKey(tid) ) {
                    for (int childId : taskChildren.get(tid)) {
                        List<Integer> deps = taskDependencies.get(childId);
                        // Converted to Integer, not to remove by index
                        deps.remove(new Integer(tid));
                        if (deps.isEmpty()) {
                            taskDependencies.remove(new Integer(childId));
                            taskDictionary.get(childId).getStatistics().setReadyTime(System.nanoTime());
                            readyList.add(taskDictionary.get(childId));
                        }

                    }
                }
                tasksToBeRemoved.add(tid);
            }

        }
        for (Integer tid: tasksToBeRemoved) {
            taskDictionary.get(tid).getStatistics().setOutTime(System.nanoTime());
            taskDictionary.get(tid).getStatistics().calcTime();
            System.out.println(taskDictionary.get(tid).getStatistics());
            
            runningList.remove(taskDictionary.get(tid));
            taskChildren.remove(tid);
            taskDictionary.remove(tid);
        }

    }

    public synchronized boolean hasNext() {
        updateGraph();
        return !readyList.isEmpty();
    }

    public synchronized RuntimeTask next() {
        updateGraph();
        RuntimeTask task = readyList.get(0);
        readyList.remove(0);
        task.getStatistics().setRunningTime(System.nanoTime());
        runningList.add(task);
        return task;
    }

    public void remove() {}

    public boolean isDone() {
        return taskDictionary.isEmpty();
    }
    
    
}
