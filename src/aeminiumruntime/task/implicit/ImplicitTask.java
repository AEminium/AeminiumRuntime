package aeminiumruntime.task.implicit;

import java.util.ArrayList;
import java.util.Collection;

import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.NonBlockingTask;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;
import aeminiumruntime.datagroup.RuntimeDataGroup;
import aeminiumruntime.graph.RuntimeGraph;
import aeminiumruntime.task.AbstractTask;
import aeminiumruntime.task.RuntimeAtomicTask;
import aeminiumruntime.task.RuntimeTask;
import aeminiumruntime.task.TaskFactory;

public abstract class ImplicitTask extends AbstractTask {

	private ImplicitTaskState state = ImplicitTaskState.WAITING_FOR_DEPENDENCIES;
	private Collection<Task> dependencies = Runtime.NO_DEPS;
	
	private Task parent = aeminiumruntime.Runtime.NO_PARENT;
	private int childCount = 0;
	private Collection<Task> dependents = new ArrayList<Task>();
	private Object result;

	public static <T extends RuntimeTask> TaskFactory<T> createFactory(final RuntimeGraph<T> graph) {
		return new TaskFactory<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public RuntimeAtomicTask<T> createAtomicTask(Body body, RuntimeDataGroup<T> datagroup, Collection<Hint> hints) {
				return new ImplicitAtomicTask<T>((RuntimeGraph<RuntimeTask>) graph, body, (RuntimeDataGroup<T>) datagroup, hints);
			}

			@Override
			public BlockingTask createBockingTask(Body body, Collection<Hint> hints) {
				return new ImplicitBlockingTask((RuntimeGraph<RuntimeTask>) graph, body, hints);
			}

			@Override
			public NonBlockingTask createNonBockingTask(Body body, Collection<Hint> hints) {
				return  new ImplicitNonBlockingTask((RuntimeGraph<RuntimeTask>) graph, body, hints);
			}
		};
	}
	
	public ImplicitTask(RuntimeGraph<RuntimeTask> graph, Body body, Collection<Hint> hints) {
		super(graph, body, hints);
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
	public void removeDependency(Collection<Task> tasks) {
		dependencies.removeAll(tasks);
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
	
	public ImplicitTaskState getTaskState() {
			return state;
	}
	
	public void setTaskState(ImplicitTaskState state) {
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

	public Collection<Hint> getHints() {
		// TODO Auto-generated method stub
		return null;
	}

	public Body getBody() {
		return body;
	}
	
	public void addDependent(ImplicitTask task) {
			dependents.add(task);
	}
	
	public Collection<Task> getDependents(){
		return dependents;
	}

	@Override
	public void setResult(Object value) {
		this.result = value;
	}
	
	@Override
	public Object getResult() {
		return result;
	}
	
	@Override
	public String toString() {
		return "Task<"+body.toString()+">" + childCount;
	}
 
	public void taskCompleted() {
		// nothing by default
	}
}
