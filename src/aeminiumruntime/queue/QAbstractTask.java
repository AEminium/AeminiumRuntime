package aeminiumruntime.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.Task;

public class QAbstractTask implements QListItem, Task, Callable<Object> {
	private QListItem prev = QTaskList.NO_ITEM;
	private QListItem next = QTaskList.NO_ITEM;
	private QTaskList list = QTaskList.NO_LIST;
	private QTaskState state = QTaskState.WAITING_FOR_DEPENDENCIES;
	private Collection<Task> dependencies = aeminiumruntime.Runtime.NO_DEPS;
	private Task parent = aeminiumruntime.Runtime.NO_PARENT;
	private int childCount = 0;
	private Body body;
	private QGraph graph;
	private Collection<Task> dependents = new ArrayList<Task>();
	
	public QAbstractTask(Body body) {
		this.body = body;
	}
	
	public void setDependencies(Collection<Task> dependencies) {
		this.dependencies = dependencies;
	}
	
	public void removeDependency(Task task) {
		dependencies.remove(task);
		if ( dependencies.isEmpty()) {
			dependencies = aeminiumruntime.Runtime.NO_DEPS;
		}
	}
	
	public final Collection<Task> getDependencies() {
		return dependencies;
	}
	
	public void setParent(Task parent) {
		this.parent = parent;
	}
	
	public Task getParent() {
		return  parent;
	}
	
	public QTaskState getTaskState() {
			return state;
	}
	
	public void setTaskState(QTaskState state) {
			this.state = state;
	}
	
	public boolean hasChildren() {
		if ( 0 < childCount) {
			return true;
		} else {
			return false;
		}
	}
	
	public void addChildTask(Task child){
		childCount++;
	}
	
	public void deleteChildTask(Task child) {
		childCount--;
	}
	
	@Override
	public QListItem getNextItem() {
		return next;
	}

	@Override
	public QListItem getPrevItem() {
		return prev;
	}

	@Override
	public void setNextItem(QListItem next) {
		this.next = next;
	}

	@Override
	public void setPrevItem(QListItem prev) {
		this.prev = prev;
	}

	@Override
	public QTaskList getTaskList() {
		return list;
	}

	@Override
	public void setTaskList(QTaskList list) {
		this.list = list;		
	}

	@Override
	public Collection<Hint> getHints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object call() throws Exception {
		body.execute(this);
		graph.taskFinshed(this);
		return null;
	}

	public void setGraph(QGraph graph) {
		this.graph = graph;
	}

	public QGraph getGraph() {
		return graph;
	}

	public void addDependent(QAbstractTask task) {
			dependents.add(task);
	}
	
	public Collection<Task> getDependents(){
		return dependents;
	}

	@Override
	public String toString() {
		return "Task<"+body.toString()+">" + childCount;
	}
 }
