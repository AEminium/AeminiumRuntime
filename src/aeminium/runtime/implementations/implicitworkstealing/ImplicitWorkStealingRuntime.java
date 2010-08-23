package aeminium.runtime.implementations.implicitworkstealing;

import java.io.File;
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
import aeminium.runtime.utils.graphviz.DiGraphViz;
import aeminium.runtime.utils.graphviz.GraphViz;
import aeminium.runtime.utils.graphviz.GraphViz.Color;
import aeminium.runtime.utils.graphviz.GraphViz.LineStyle;
import aeminium.runtime.utils.graphviz.GraphViz.RankDir;

public final class ImplicitWorkStealingRuntime implements Runtime {
	public final ImplicitGraph graph;
	public final BlockingWorkStealingScheduler scheduler;
	protected final EventManager eventManager;
	protected DiGraphViz digraphviz;
	protected final boolean enableGraphViz = Configuration.getProperty(getClass(), "enableGraphViz", false);
	protected final String graphVizName    = Configuration.getProperty(getClass(), "graphVizName", "GraphVizOutput");
	protected final int ranksep            = Configuration.getProperty(getClass(), "ranksep", 1);
	protected final RankDir rankdir        = GraphViz.getDefaultValue(Configuration.getProperty(getClass(), "rankdir", "TB"), RankDir.TB, RankDir.values());
	
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
		if ( enableGraphViz ) {
			digraphviz = new DiGraphViz(graphVizName, ranksep, rankdir);
		}
	}
	
	@Override
	public final void shutdown() throws RuntimeError {
		graph.waitToEmpty();
		scheduler.shutdown();
		eventManager.shutdown();
		graph.shutdown();
		if ( enableGraphViz ) {
			digraphviz.dump(new File(digraphviz.getName()+".dot"));
			digraphviz = null;
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
		if ( enableGraphViz ) {
			ImplicitTask itask = (ImplicitTask)task;
			digraphviz.addNode(itask.hashCode(), itask.body.toString());
			if ( parent != NO_PARENT ) {
				digraphviz.addConnection(itask.hashCode(), parent.hashCode(), LineStyle.DASHED, Color.RED);
			}
			if ( deps != NO_DEPS ) {
				for ( Task dep : deps) {
					digraphviz.addConnection(itask.hashCode(), dep.hashCode(), LineStyle.SOLID, Color.BLUE);
				}
 			}
		}
		graph.addTask((ImplicitTask)task, parent, deps);
	}
}
