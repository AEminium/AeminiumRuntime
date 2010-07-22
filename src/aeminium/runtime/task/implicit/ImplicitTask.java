package aeminium.runtime.task.implicit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.AbstractTaskFactory;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.TaskFactory;

public abstract class ImplicitTask extends AbstractTask<ImplicitTask> {

	private ImplicitTaskState state = ImplicitTaskState.UNSCHEDULED;
	private Collection<Task> dependencies = Runtime.NO_DEPS;
	
	private Task parent = aeminium.runtime.Runtime.NO_PARENT;
	private int childCount = 0;
	private Collection<Task> dependents = new ArrayList<Task>();
	private Object result;

	public static TaskFactory<ImplicitTask> createFactory(final RuntimeGraph<ImplicitTask> graph, EnumSet<Flags> flags) {
		return new AbstractTaskFactory<ImplicitTask>(flags) {
			@Override 
			public void init() {}
			@Override 
			public void shutdown() {}
			
			@SuppressWarnings("unchecked")
			@Override
			public RuntimeAtomicTask<ImplicitTask> createAtomicTask(Body body, RuntimeDataGroup<ImplicitTask> datagroup, Collection<Hints> hints) {
				return new ImplicitAtomicTask((RuntimeGraph<ImplicitTask>) graph, body, (RuntimeDataGroup<ImplicitTask>) datagroup, hints, flags);
			}

			@Override
			public BlockingTask createBockingTask(Body body, Collection<Hints> hints) {
				return new ImplicitBlockingTask((RuntimeGraph<ImplicitTask>) graph, body, hints, flags);
			}

			@Override
			public NonBlockingTask createNonBockingTask(Body body, Collection<Hints> hints) {
				return  new ImplicitNonBlockingTask((RuntimeGraph<ImplicitTask>) graph, body, hints, flags);
			}
		};
	}
	
	public ImplicitTask(RuntimeGraph<ImplicitTask> graph, Body body, Collection<Hints> hints, EnumSet<Flags> flags) {
		super(graph, body, hints, flags);
	}

	public void setDependencies(Collection<Task> dependencies) {
		this.dependencies = dependencies;
	}
	
	public void removeDependency(Task task) {
		dependencies.remove(task);
		if ( dependencies.isEmpty()) {
			dependencies = aeminium.runtime.Runtime.NO_DEPS;
		}
	}
	public void removeDependency(Collection<Task> tasks) {
		dependencies.removeAll(tasks);
		if ( dependencies.isEmpty()) {
			dependencies = aeminium.runtime.Runtime.NO_DEPS;
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

	public Collection<Hints> getHints() {
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
