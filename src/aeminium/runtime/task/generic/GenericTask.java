package aeminium.runtime.task.generic;

import java.util.Collection;
import java.util.EnumSet;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.AbstractTaskFactory;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.TaskFactory;

public abstract class GenericTask extends AbstractTask<GenericTask> {

	public GenericTask(RuntimeGraph<GenericTask> graph, Body body, Collection<Hints> hints, EnumSet<Flags> flags) {
		super(graph, body, hints, flags);
	}
	
	public static TaskFactory<GenericTask> createFactory(final RuntimeGraph<GenericTask> graph, final EnumSet<Flags> flags) {
		return new AbstractTaskFactory<GenericTask>(flags) {
	
			@Override 
			public void init() {}
			@Override 
			public void shutdown() {}
			
			@SuppressWarnings("unchecked")
			@Override
			public RuntimeAtomicTask<GenericTask> createAtomicTask(Body body, RuntimeDataGroup<GenericTask> datagroup, Collection<Hints> hints) {
				return new GenericAtomicTask(graph, body, (RuntimeDataGroup<GenericTask>) datagroup, hints, flags);
			}

			@Override
			public BlockingTask createBockingTask(Body body, Collection<Hints> hints) {
				return new GenericBlockingTask((RuntimeGraph<GenericTask>) graph, body, hints, flags);
			}

			@Override
			public NonBlockingTask createNonBockingTask(Body body, Collection<Hints> hints) {
				return  new GenericNonBlockingTask( graph, body, hints, flags);
			}
		};
	}

	@Override
	public String toString() {
		return "GenericTask<"+body+">";
	}
}
