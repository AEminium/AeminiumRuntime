package aeminiumruntime.queue;

import java.util.ArrayList;
import java.util.Collection;

import aeminiumruntime.AtomicTask;
import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;
import aeminiumruntime.NonBlockingTask;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;


public class QRuntime extends Runtime {
	private QHybridScheduler scheduler;
	private QGraph taskGraph;

	private enum QRuntimeState {
		UNINITIALIZED,
		INITIALIZED,
		SHUTTING_DOWN
	};
	private QRuntimeState state = QRuntimeState.UNINITIALIZED; 
	
	@Override
	public AtomicTask createAtomicTask(Body b, DataGroup g) {
		assert( state == QRuntimeState.INITIALIZED);
		return new QAtomicTask(b, g);
	}

	@Override
	public BlockingTask createBlockingTask(Body b) {
		assert( state == QRuntimeState.INITIALIZED);
		return new QBlockingTask(b);
	}

	@Override
	public DataGroup createDataGroup() {
		assert( state == QRuntimeState.INITIALIZED);
		return new QDataGroup();
	}

	@Override
	public NonBlockingTask createNonBlockingTask(Body b) {
		return new QNonBlockingTask(b);
	}

	@Override
	public void init() {
		assert( state == QRuntimeState.UNINITIALIZED);
		scheduler = new QHybridScheduler();
		taskGraph = new QGraph(scheduler);
		assert ( state == QRuntimeState.INITIALIZED );
	}

	@Override
	public boolean schedule(Task task, Collection<Task> deps) {
		assert ( state == QRuntimeState.INITIALIZED );
		assert ( task instanceof QAbstractTask );
		((QAbstractTask)task).setDependencies(deps);
		return taskGraph.addTask(((QAbstractTask)task));
	}

	@Override
	public boolean schedule(Task task, Task parent, Collection<Task> deps) {
		assert ( state == QRuntimeState.INITIALIZED );
		assert ( task instanceof QAbstractTask );
		QAbstractTask at = (QAbstractTask)task;
		if ( deps != NO_DEPS ) {
			at.setDependencies(new ArrayList<Task>(deps));
		} else {
			at.setDependencies(NO_DEPS);
		}
		at.setParent(parent);
		if ( parent != NO_PARENT ) {
			at.addChildTask(task);
		}
		return taskGraph.addTask(((QAbstractTask)task));
	}

	@Override
	public void shutdown() {
		assert ( state == QRuntimeState.INITIALIZED );
		state = QRuntimeState.SHUTTING_DOWN;
		
		taskGraph.waitUntilEmpty();
		scheduler.shutdown();
		
		scheduler = null;
		taskGraph = null;
		
		state = QRuntimeState.UNINITIALIZED;
		assert ( state == QRuntimeState.UNINITIALIZED );
	}

}
