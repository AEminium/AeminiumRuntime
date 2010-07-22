package aeminium.runtime.task.generic;

import java.util.Collection;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskFactory;

public abstract class GenericTask extends AbstractTask<GenericTask> {

	public GenericTask(RuntimeGraph<GenericTask> graph, Body body, Collection<Hints> hints) {
		super(graph, body, hints);
	}
	
	public static TaskFactory<GenericTask> createFactory(final RuntimeGraph<GenericTask> graph) {
		return new TaskFactory<GenericTask>() {
			@Override 
			public void init() {}
			@Override 
			public void shutdown() {}
			
			@SuppressWarnings("unchecked")
			@Override
			public RuntimeAtomicTask<GenericTask> createAtomicTask(Body body, RuntimeDataGroup<GenericTask> datagroup, Collection<Hints> hints) {
				return new GenericAtomicTask(graph, body, (RuntimeDataGroup<GenericTask>) datagroup, hints);
			}

			@Override
			public BlockingTask createBockingTask(Body body, Collection<Hints> hints) {
				return new GenericBlockingTask((RuntimeGraph<GenericTask>) graph, body, hints);
			}

			@Override
			public NonBlockingTask createNonBockingTask(Body body, Collection<Hints> hints) {
				return  new GenericNonBlockingTask( graph, body, hints);
			}
		};
	}

	@Override
	public String toString() {
		return "GenericTask<"+body+">";
	}
}
