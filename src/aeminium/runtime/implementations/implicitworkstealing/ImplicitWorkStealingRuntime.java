package aeminium.runtime.implementations.implicitworkstealing;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
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
import aeminium.runtime.ErrorHandler;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.implicitworkstealing.datagroup.FifoDataGroup;
import aeminium.runtime.implementations.implicitworkstealing.datagroup.NestedAtomicTasksDataGroup;
import aeminium.runtime.implementations.implicitworkstealing.datagroup.ImplicitWorkStealingRuntimeDataGroup;
import aeminium.runtime.implementations.implicitworkstealing.datagroup.NestedAtomicTasksDataGroup.ImplicitWorkStealingRuntimeDataGroupFactory;
import aeminium.runtime.implementations.implicitworkstealing.error.ErrorManager;
import aeminium.runtime.implementations.implicitworkstealing.error.ErrorManagerAdapter;
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
	protected ExecutorService executorService;
	protected final EventManager eventManager;
	protected DiGraphViz digraphviz;
	protected ErrorManager errorManager;
	protected State state = State.UNINITIALIZED;  
	protected ImplicitWorkStealingRuntimeDataGroupFactory dataGroupFactory;
	protected final boolean nestedAtomicTasks = Configuration.getProperty(getClass(), "nestedAtomicTasks", false);
	protected final boolean enableGraphViz    = Configuration.getProperty(getClass(), "enableGraphViz", false);
	protected final String graphVizName       = Configuration.getProperty(getClass(), "graphVizName", "GraphVizOutput");
	protected final int ranksep               = Configuration.getProperty(getClass(), "ranksep", 1);
	protected final RankDir rankdir           = GraphViz.getDefaultValue(Configuration.getProperty(getClass(), "rankdir", "TB"), RankDir.TB, RankDir.values());
	
	public enum State {
		UNINITIALIZED,
		INITIALIZED
	}
	
	public ImplicitWorkStealingRuntime() {
		graph        = new ImplicitGraph(this);
		scheduler    = new BlockingWorkStealingScheduler(this);
		eventManager = new EventManager();
		errorManager = new ErrorManagerAdapter();
	}
	
	@Override
	public final void init()  {
		if ( state != State.UNINITIALIZED ) {
			throw new Error("Cannot initialize runtime multiple times.");
		}
		eventManager.init();
		graph.init(eventManager);
		scheduler.init(eventManager);
		if ( enableGraphViz ) {
			digraphviz = new DiGraphViz(graphVizName, ranksep, rankdir);
		}
		dataGroupFactory = new ImplicitWorkStealingRuntimeDataGroupFactory() {	
			@Override
			public ImplicitWorkStealingRuntimeDataGroup create() {
				return new FifoDataGroup();
			}
		};
		if ( nestedAtomicTasks ) {
			final ImplicitWorkStealingRuntimeDataGroupFactory innerFactory = dataGroupFactory;
			dataGroupFactory = new ImplicitWorkStealingRuntimeDataGroupFactory() {
				@Override
				public ImplicitWorkStealingRuntimeDataGroup create() {
					return new NestedAtomicTasksDataGroup(innerFactory);
				}
			};
		}
		state = State.INITIALIZED;
	}
	
	@Override
	public final void shutdown()  {
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
			dataGroupFactory = null;
			state = State.UNINITIALIZED;
		}
	}
	
	@Override
	public final AtomicTask createAtomicTask(Body body, DataGroup datagroup, short hints) {
		return new ImplicitAtomicTask(body, (ImplicitWorkStealingRuntimeDataGroup)datagroup, hints);
	}

	@Override
	public final BlockingTask createBlockingTask(Body body, short hints)
			 {
		return new ImplicitBlockingTask(body, hints);
	}
	
	@Override
	public final NonBlockingTask createNonBlockingTask(Body body, short hints)
			 {
		return new ImplicitNonBlockingTask(body, hints);
	}

	@Override
	public final DataGroup createDataGroup()  {
		return dataGroupFactory.create();
	}

	@Override
	public final void schedule(Task task, Task parent, Collection<Task> deps)
			 {
		if ( enableGraphViz ) {
			ImplicitTask itask = (ImplicitTask)task;
			digraphviz.addNode(itask.hashCode(), itask.body.toString());
			if ( parent != NO_PARENT ) {
				digraphviz.addConnection(itask.hashCode(), parent.hashCode(), LineStyle.DASHED, Color.RED, "");
			}
			if ( deps != NO_DEPS ) {
				for ( Task dep : deps) {
					digraphviz.addConnection(itask.hashCode(), dep.hashCode(), LineStyle.SOLID, Color.BLUE, "");
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
	
	public DiGraphViz getGraphViz() {
		return this.digraphviz;
	}

	@Override
	public final void addErrorHandler(final ErrorHandler eh) {
		errorManager.addErrorHandler(eh);		
	}

	@Override
	public final void removeErrorHandler(final ErrorHandler eh) {
		errorManager.removeErrorHandler(eh);
	}
	
	public final ErrorManager getErrorManager() {
		return errorManager;
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
			List<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());
			for ( Callable<T> c : tasks) {
				futures.add(submit(c, timeout, unit));
			}
			return futures;
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
		public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
		        throws InterruptedException, ExecutionException, TimeoutException {
			final long start = System.nanoTime();
			final Iterator<?> it = tasks.iterator();
			Callable<T> current = null;
			while ( System.nanoTime() < start + unit.toNanos(timeout) ) {
				if ( current == null && it.hasNext() ) {
					@SuppressWarnings("unchecked")
					Callable<T> tmp = (Callable<T>)it.next();
					current = tmp;
				} else if ( !it.hasNext() ) {
					break;
				}
				Future<T> f = submit(current);
				T result = f.get();
				if ( result != null && !(result instanceof Exception)) {
					return result;
				} else {
					current = null;
				}
			}
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
			@SuppressWarnings("unchecked")
			List<Runnable> result = (List<Runnable>)Collections.EMPTY_LIST;
			return result;
		}

		public <T> Future<T> submit(Callable<T> task, long timeout, TimeUnit unit) {
			@SuppressWarnings("unchecked")
			final RunnableFutureTask rft = new RunnableFutureTask((Callable<Object>)task);
			ImplicitTask aetask = (ImplicitTask) rt.createBlockingTask(rft, NO_HINTS);
			rft.setTask(aetask);
			rft.setTimeOut(System.nanoTime()+unit.toNanos(timeout));
			rt.schedule(aetask, NO_PARENT, NO_DEPS);
			@SuppressWarnings("unchecked")
			Future<T> result = (Future<T>) rft;
			return result;
		}
		
		@Override
		public <T> Future<T> submit(Callable<T> task) {
			@SuppressWarnings("unchecked")
			final RunnableFutureTask rft = new RunnableFutureTask((Callable<Object>)task);
			ImplicitTask aetask = (ImplicitTask) rt.createBlockingTask(rft, NO_HINTS);
			rft.setTask(aetask);
			rt.schedule(aetask, NO_PARENT, NO_DEPS);
			@SuppressWarnings("unchecked")
			Future<T> result = (Future<T>)rft;
			return result;
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
			@SuppressWarnings("unchecked")
			Future<T> tmp = (Future<T>) submit(task);
			return tmp;
		}

		@Override
		public void execute(Runnable command) {
			submit(command);			
		}
	
		protected class RunnableFutureTask implements RunnableFuture<Object>, Body{
			private final Callable<Object> body;
			private ImplicitTask task;
			private boolean cancelled = false;
			private long timeout = 0;
			
			RunnableFutureTask(final Callable<Object> body) {
				super();
				this.body = body;
			}
			
			public void setTask(ImplicitTask task) {
				this.task = task;
			}
			
			public void setTimeOut(long timeout) {
				this.timeout = timeout;
			}
			
			@Override
			public void run() {
				throw new Error("Cannot execute the run method.");				
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				cancelled = ImplicitWorkStealingExecutorService.this.rt.scheduler.cancelTask(task);
				return cancelled;
			}

			@Override
			public Object get() throws InterruptedException, ExecutionException {
				Object result = task.getResult();
				if ( result instanceof Exception ) {
					throw new ExecutionException((Exception)result);
				} else {
					return result;
				}
			}

			@Override
			public Object get(long timeout, TimeUnit unit) 	throws InterruptedException, ExecutionException, TimeoutException {
				final long start = System.nanoTime();
				while ( System.nanoTime() < start + unit.toNanos(timeout) ) {
					if ( !task.isCompleted() ) {
						// TODO: make step depending
						Thread.sleep(1);
					}
				}
				if ( task.isCompleted() ) {
					Object result =  task.getResult();
					if ( result instanceof Exception ) {
						throw new ExecutionException((Exception)result);
					} else {
						return result;
					}
				} else {
					throw new TimeoutException();
				}
			}

			@Override
			public boolean isCancelled() {
				return cancelled;
			}

			@Override
			public boolean isDone() {
				return task.isCompleted();
			}

			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				if ( timeout < System.nanoTime() ) {
					current.setResult(body.call());
				} else {
					cancelled = true;
				}
			}
			
		}
	}



}
