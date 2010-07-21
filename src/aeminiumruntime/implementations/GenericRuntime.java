package aeminiumruntime.implementations;

import java.util.Collection;

import aeminiumruntime.AtomicTask;
import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;
import aeminiumruntime.Hint;
import aeminiumruntime.NonBlockingTask;
import aeminiumruntime.Task;
import aeminiumruntime.datagroup.DataGroupFactory;
import aeminiumruntime.datagroup.RuntimeDataGroup;
import aeminiumruntime.graph.RuntimeGraph;
import aeminiumruntime.prioritizer.RuntimePrioritizer;
import aeminiumruntime.scheduler.RuntimeScheduler;
import aeminiumruntime.task.RuntimeTask;
import aeminiumruntime.task.TaskFactory;
import aeminiumruntime.task.implicit.ImplicitTask;

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