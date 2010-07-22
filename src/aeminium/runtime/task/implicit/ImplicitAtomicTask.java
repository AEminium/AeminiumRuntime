package aeminium.runtime.task.implicit;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.task.RuntimeAtomicTask;

public class ImplicitAtomicTask extends ImplicitTask implements RuntimeAtomicTask<ImplicitTask> {
	private final RuntimeDataGroup<ImplicitTask> datagroup;
	
	public ImplicitAtomicTask(RuntimeGraph<ImplicitTask> graph, Body body, RuntimeDataGroup<ImplicitTask> datagroup, Collection<Hints> hints) {
		super(graph, body, hints);
		this.datagroup = datagroup;
	}
	
	public RuntimeDataGroup<ImplicitTask> getDataGroup() {
		return datagroup;
	}

	@Override
	public Object call() throws Exception {
		boolean locked = datagroup.trylock(this);
		if ( locked ) {
			getBody().execute(this);
			graph.taskFinished(this);
		}
		return null;
	}

	@Override 
	public void taskCompleted() {
		datagroup.unlock();
	}

}