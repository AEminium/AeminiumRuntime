package aeminium.runtime.implementations.implicitworkstealing;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTaskState;
import aeminium.runtime.utils.graphviz.DiGraphViz;
import aeminium.runtime.utils.graphviz.GraphViz;
import aeminium.runtime.utils.graphviz.GraphViz.Color;
import aeminium.runtime.utils.graphviz.GraphViz.LineStyle;
import aeminium.runtime.utils.graphviz.GraphViz.RankDir;

public final class ImplicitWorkStealingRuntime implements Runtime {
	public final ImplicitGraph graph;
	public final BlockingWorkStealingScheduler scheduler;
	protected ExecutorService executorService;
	protected final EventManager eventManager;
	protected DiGraphViz digraphviz;
	protected State state = State.UNINITIALIZED;  
	protected final boolean enableGraphViz = Configuration.getProperty(getClass(), "enableGraphViz", false);
	protected final String graphVizName    = Configuration.getProperty(getClass(), "graphVizName", "GraphVizOutput");
	protected final int ranksep            = Configuration.getProperty(getClass(), "ranksep", 1);
	protected final RankDir rankdir        = GraphViz.getDefaultValue(Configuration.getProperty(getClass(), "rankdir", "TB"), RankDir.TB, RankDir.values());
	
	public enum State {
		UNINITIALIZED,
		INITIALIZED
	}
	
	public ImplicitWorkStealingRuntime() {
		graph        = new ImplicitGraph(this);
		scheduler    = new BlockingWorkStealingScheduler(this);
		eventManager = new EventManager();
	}
	
	@Override
	public final void init() throws RuntimeError {
		if ( state != State.UNINITIALIZED ) {
			throw new RuntimeError("Cannot initialize runtime multiple times.");
		}
		eventManager.init();
		graph.init(eventManager);
		scheduler.init(eventManager);
		if ( enableGraphViz ) {
			digraphviz = new DiGraphViz(graphVizName, ranksep, rankdir);
		}
		state = State.INITIALIZED;
	}
	
	@Override
	public final void shutdown() throws RuntimeError {
		if ( state != State.UNINITIALIZED ) {
			graph.waitToEmpty();
			scheduler.shutdown();
			eventManager.shutdown();
			graph.shutdown();
			if ( enableGraphViz ) {
				digraphviz.dump(new File(digraphviz.getName()+".dot"));
				digraphviz = null;
			}
			executorService = null;
			state = State.UNINITIALIZED;
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

	public final ExecutorService getExecutorService() {
		synchronized (this) {
			if ( executorService == null ) {
				executorService = new ImplicitWorkStealingExecutorService(this);
			}
			return executorService;
		}
	}
	
	protected final static class ImplicitWorkStealingExecutorService implements ExecutorService {
		private ImplicitWorkStealingRuntime rt;		
		
		public ImplicitWorkStealingExecutorService(ImplicitWorkStealingRuntime rt) {
			super();
			this.rt = rt;
		}

		@Override
		public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
			throw new InterruptedException();
		}

		@Override
		public <T> List<Future<T>> invokeAll(
				Collection<? extends Callable<T>> tasks)
				throws InterruptedException {
			List<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());
			for ( Callable<T> c : tasks) {
				futures.add(submit(c));
			}
			return futures;
		}

		@Override
		public <T> List<Future<T>> invokeAll(
				Collection<? extends Callable<T>> tasks, long timeout,
				TimeUnit unit) throws InterruptedException {
			// TODO Auto-generated method stub
			return invokeAll(tasks);
		}

		@Override
		public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
				throws InterruptedException, ExecutionException {
			Exception ex = null;
			for ( Callable<T> c : tasks) {
				Future<T> f = submit(c);
				if ( f.get() == null || (f.get() != null && !(f.get() instanceof Exception ))) {
					return f.get();
				} else if ( f.get() != null && f.get() instanceof Exception  ) {
					ex = (Exception)f.get();
				}
			}
			throw new ExecutionException(ex);
		}

		@Override
		public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
				long timeout, TimeUnit unit) throws InterruptedException,
				ExecutionException, TimeoutException {
			// TODO Auto-generated method stub
			return invokeAny(tasks);
		}

		@Override
		public boolean isShutdown() {
			return rt.state == State.UNINITIALIZED;
		}

		@Override
		public boolean isTerminated() {
			return rt.state == State.UNINITIALIZED;
		}

		@Override
		public void shutdown() {
			rt.shutdown();			
		}

		@Override
		public List<Runnable> shutdownNow() {
			shutdown();
			return Collections.EMPTY_LIST;
		}

		@Override
		public <T> Future<T> submit(Callable<T> task) {
			RunnableFutureTask rft = new RunnableFutureTask((Callable<Object>)task);
			ImplicitTask aetask = (ImplicitTask) rt.createBlockingTask(rft, NO_HINTS);
			rft.setTask(aetask);
			rt.schedule(aetask, NO_PARENT, NO_DEPS);
			return (Future<T>)rft;
		}

		@Override
		public Future<?> submit(final Runnable task) {
			return submit(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					task.run();
					return null;
				}
				
			});
		}

		@Override
		public <T> Future<T> submit(Runnable task, T result) {
			return (Future<T>) submit(task);
		}

		@Override
		public void execute(Runnable command) {
			submit(command);			
		}
	
		protected class RunnableFutureTask implements RunnableFuture<Object>, Body{
			private final Callable<Object> body;
			private ImplicitTask task;
			
			RunnableFutureTask(final Callable<Object> body) {
				super();
				this.body = body;
			}
			
			RunnableFutureTask(final Runnable body) {
				super();
				this.body = new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						body.run();
						return null;
					}
					
				};
			}
			
			public void setTask(ImplicitTask task) {
				this.task = task;
			}
			
			@Override
			public void run() {
				throw new RuntimeError("Cannot execute the run method.");				
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return ImplicitWorkStealingExecutorService.this.rt.scheduler.cancelTask(task);
			}

			@Override
			public Object get() throws InterruptedException, ExecutionException {
				return task.getResult();
			}

			@Override
			public Object get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				if ( task.isCompleted() ) {
					return task.getResult();
				} else {
					throw new InterruptedException("Not completed yet, try again.");
				}
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return task.state == ImplicitTaskState.COMPLETED;
			}

			@Override
			public boolean isDone() {
				return task.isCompleted();
			}

			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				current.setResult(body.call());
			}
			
		}
	}
}
