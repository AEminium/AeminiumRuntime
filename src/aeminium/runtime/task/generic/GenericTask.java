package aeminium.runtime.task.generic;

import java.util.Collection;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.Hint;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.task.AbstractTask;
import aeminium.runtime.task.RuntimeAtomicTask;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskFactory;

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
