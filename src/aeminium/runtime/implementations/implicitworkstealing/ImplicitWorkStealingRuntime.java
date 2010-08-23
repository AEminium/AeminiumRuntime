package aeminium.runtime.implementations.implicitworkstealing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import aeminium.runtime.AtomicTask;
import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.Runtime;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.datagroup.FifoDataGroup;
import aeminium.runtime.implementations.implicitworkstealing.events.EventManager;
import aeminium.runtime.implementations.implicitworkstealing.graph.ImplicitGraph;
import aeminium.runtime.implementations.implicitworkstealing.scheduler.BlockingWorkStealingScheduler;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitAtomicTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitBlockingTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitNonBlockingTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTask;

public final class ImplicitWorkStealingRuntime implements Runtime {
	public final ImplicitGraph graph;
	public final BlockingWorkStealingScheduler scheduler;
	protected final EventManager eventManager;
	protected final boolean trace = Configuration.getProperty(getClass(), "trace", false);
	protected StringBuilder tracerConnections;
	protected StringBuilder tracerNodes;
	
	public ImplicitWorkStealingRuntime() {
		graph        = new ImplicitGraph(this);
		scheduler    = new BlockingWorkStealingScheduler(this);
		eventManager = new EventManager();
	}
	
	@Override
	public final void init() throws RuntimeError {
		eventManager.init();
		graph.init(eventManager);
		scheduler.init(eventManager);
		if ( trace ) {
			tracerConnections = new StringBuilder();
			tracerNodes       = new StringBuilder();
		}
	}
	
	@Override
	public final void shutdown() throws RuntimeError {
		graph.waitToEmpty();
		scheduler.shutdown();
		eventManager.shutdown();
		graph.shutdown();
		if ( trace ) {
			try {
				FileWriter fw = new FileWriter("trace.dot");
				fw.append("digraph AeminiumRT { \n");
				fw.append("    rankdir=BT\n");
				fw.append("    ranksep=1\n");
				fw.append(tracerNodes.toString());
				fw.append(tracerConnections.toString());
				fw.append("}\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public final AtomicTask createAtomicTask(Body body, DataGroup datagroup, short hints)
			throws RuntimeError {
		return new ImplicitAtomicTask(body, (FifoDataGroup)datagroup, hints);
	}

	@Override
	public final BlockingTask createBlockingTask(Body body, short hints)
			throws RuntimeError {
		return new ImplicitBlockingTask(body, hints);
	}
	
	@Override
	public final NonBlockingTask createNonBlockingTask(Body body, short hints)
			throws RuntimeError {
		return new ImplicitNonBlockingTask(body, hints);
	}

	@Override
	public final DataGroup createDataGroup() throws RuntimeError {
		return new FifoDataGroup();
	}

	@Override
	public final void schedule(Task task, Task parent, Collection<Task> deps)
			throws RuntimeError {
		if ( trace ) {
			ImplicitTask itask = (ImplicitTask)task;
			tracerNodes.append("    "+ task.hashCode()+" [label=\""+itask.body.toString()+"\"]\n");
			if ( parent != NO_PARENT ) {
				tracerConnections.append("    "+task.hashCode()+" -> "+parent.hashCode()+" [color=\"red\", style=\"dashed\"]\n");
			}
			if ( deps != NO_DEPS ) {
				for ( Task dep : deps) {
					tracerConnections.append("    "+task.hashCode()+" -> "+dep.hashCode()+" [color=\"blue\"]\n");
				}
 			}
		}
		graph.addTask((ImplicitTask)task, parent, deps);
	}
}
