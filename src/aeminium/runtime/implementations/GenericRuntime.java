package aeminium.runtime.implementations;

import java.util.Collection;

import aeminium.runtime.AtomicTask;
import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.Hint;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.Task;
import aeminium.runtime.datagroup.DataGroupFactory;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskFactory;
import aeminium.runtime.task.implicit.ImplicitTask;

public class GenericRuntime<T extends RuntimeTask> extends AbstractRuntime {
	private final RuntimeScheduler<T> scheduler;
	@SuppressWarnings("unused")
	private final RuntimePrioritizer<T> prioritizer;
	private final RuntimeGraph<T> graph;
	private final DataGroupFactory<T> dataGroupFactory;
	private final TaskFactory<T> taskFactory;

	private enum GenericRuntimeState {
		UNINITIALIZED,
		INITIALIZED,
		SHUTTING_DOWN
	};
	private GenericRuntimeState state = GenericRuntimeState.UNINITIALIZED; 
	
	public GenericRuntime(RuntimeScheduler<T> scheduler,
						  RuntimePrioritizer<T> prioritizer,
						  RuntimeGraph<T> graph,
						  DataGroupFactory<T> dataGroupFactory,
						  TaskFactory<T> taskFactory) {
		this.scheduler = scheduler;
		this.prioritizer = prioritizer;
		this.graph = graph;
		this.dataGroupFactory = dataGroupFactory;
		this.taskFactory = taskFactory;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public AtomicTask createAtomicTask(Body body, DataGroup group, Collection<Hint> hints) {
		assert( state == GenericRuntimeState.INITIALIZED);
		return taskFactory.createAtomicTask(body, (RuntimeDataGroup<T>)group, hints);
	}

	@Override
	public BlockingTask createBlockingTask(Body body, Collection<Hint> hints) {
		assert( state == GenericRuntimeState.INITIALIZED);
		return taskFactory.createBockingTask(body, hints);
	}

	@Override
	public NonBlockingTask createNonBlockingTask(Body body, Collection<Hint> hints) {
		return taskFactory.createNonBockingTask(body, hints);
	}
	
	@Override
	public DataGroup createDataGroup() {
		assert( state == GenericRuntimeState.INITIALIZED);
		return dataGroupFactory.createDataGroup(scheduler);
	}

	@Override
	public void init() {
		assert( state == GenericRuntimeState.UNINITIALIZED);
		state = GenericRuntimeState.INITIALIZED;
		assert ( state == GenericRuntimeState.INITIALIZED );
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void schedule(Task task, Task parent, Collection<Task> deps) {
		assert ( state == GenericRuntimeState.INITIALIZED );
		assert ( task instanceof ImplicitTask );
		assert ( parent instanceof ImplicitTask );
		graph.addTask((T)task, parent, (Collection<T>)deps);
	}

	@Override
	public void shutdown() {
		assert ( state == GenericRuntimeState.INITIALIZED );
		state = GenericRuntimeState.SHUTTING_DOWN;
		
		graph.waitToEmpty();
		scheduler.shutdown();
		
		state = GenericRuntimeState.UNINITIALIZED;
		assert ( state == GenericRuntimeState.UNINITIALIZED );
	}

}