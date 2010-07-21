package aeminiumruntime.task.generic;

import java.util.Collection;

import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;
import aeminiumruntime.Hint;
import aeminiumruntime.NonBlockingTask;
import aeminiumruntime.datagroup.RuntimeDataGroup;
import aeminiumruntime.graph.RuntimeGraph;
import aeminiumruntime.task.AbstractTask;
import aeminiumruntime.task.RuntimeAtomicTask;
import aeminiumruntime.task.RuntimeTask;
import aeminiumruntime.task.TaskFactory;

public abstract class GenericTask extends AbstractTask {

	public GenericTask(RuntimeGraph<RuntimeTask> graph, Body body, Collection<Hint> hints) {
		super(graph, body, hints);
	}
	
	public static <T extends RuntimeTask> TaskFactory<T> createFactory(final RuntimeGraph<T> graph) {
		return new TaskFactory<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public RuntimeAtomicTask<T> createAtomicTask(Body body, RuntimeDataGroup<T> datagroup, Collection<Hint> hints) {
				return new GenericAtomicTask<T>((RuntimeGraph<RuntimeTask>) graph, body, (RuntimeDataGroup<T>) datagroup, hints);
			}

			@Override
			public BlockingTask createBockingTask(Body body, Collection<Hint> hints) {
				return new GenericBlockingTask((RuntimeGraph<RuntimeTask>) graph, body, hints);
			}

			@Override
			public NonBlockingTask createNonBockingTask(Body body, Collection<Hint> hints) {
				return  new GenericNonBlockingTask((RuntimeGraph<RuntimeTask>) graph, body, hints);
			}
		};
	}

}
