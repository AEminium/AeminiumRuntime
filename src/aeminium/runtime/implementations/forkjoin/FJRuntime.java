package aeminium.runtime.implementations.forkjoin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import aeminium.runtime.AtomicTask;
import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.ErrorHandler;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.forkjoin.tasks.FJAtomicTask;
import aeminium.runtime.implementations.forkjoin.tasks.FJBlockingTask;
import aeminium.runtime.implementations.forkjoin.tasks.FJDataGroup;
import aeminium.runtime.implementations.forkjoin.tasks.FJNonBlockingTask;
import aeminium.runtime.implementations.forkjoin.tasks.FJTask;
import aeminium.runtime.implementations.implicitworkstealing.task.ImplicitTaskState;

public class FJRuntime implements Runtime {

	ForkJoinPool pool;
	ErrorHandler er;
	
	@Override
	public void init() {
		pool = new ForkJoinPool();
	}

	@Override
	public void shutdown() {
		if (pool != null) pool.shutdown();
	}

	@Override
	public void waitToEmpty() {
		while (!pool.isTerminated())
			try {
				pool.awaitTermination(1000, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
			}
	}

	@Override
	public void schedule(Task task, Task parent, Collection<Task> deps) {
		this.handleSubmissionOfTask(task, parent, deps);
	}

	@Override
	public boolean parallelize() {
		return pool.getQueuedSubmissionCount() >= 5;
	}

	@Override
	public DataGroup createDataGroup() {
		return new FJDataGroup();
	}

	@Override
	public BlockingTask createBlockingTask(Body b, short hints) {
		return new FJBlockingTask(b, hints, this);
	}

	@Override
	public NonBlockingTask createNonBlockingTask(Body b, short hints) {
		return new FJNonBlockingTask(b, hints, this);
	}

	@Override
	public AtomicTask createAtomicTask(Body b, DataGroup g, short hints) {
		return new FJAtomicTask(b, hints, this, g);
	}

	@Override
	public ExecutorService getExecutorService() {
		return null; // Not implemented in this runtime
	}
	
	
	public void handleSubmissionOfTask(Task task, Task parent,
			Collection<Task> deps) {
		FJTask t = (FJTask) task;
		t.runtime = this;
		
		if (parent != Runtime.NO_PARENT) {
			t.parent = (FJTask) parent;
			t.parent.childCount++;
		}
		
		if (deps == Runtime.NO_DEPS || deps == null || deps.size() == 0) {
			handleTaskGo(t);
		} else {
			t.state = ImplicitTaskState.WAITING_FOR_DEPENDENCIES;
			synchronized(t) {
				t.depCount = deps.size();
				for (Task depu : deps) {
					FJTask dep = (FJTask) depu;
					synchronized (dep) {
						dep.dependents.add(t);
					}
				}
			}
		}
	}

	protected void handleTaskGo(FJTask t) {
		if (t == null) System.out.println("FAIL!");
		t.state = ImplicitTaskState.WAITING_IN_QUEUE;
		pool.invoke(t);
	}
	
	public void handleStartOfTask(FJTask t) {
		t.state = ImplicitTaskState.RUNNING;
	}

	public void handleEndOfTask(FJTask t) {
		ArrayList<FJTask> tasksToRelease = new ArrayList<FJTask>();
		ArrayList<FJTask> tasksToFinish = new ArrayList<FJTask>();
		if (t.childCount > 0) {
			t.state = ImplicitTaskState.WAITING_FOR_CHILDREN;
		} else {
			// Release current dependents
			synchronized (t) {
				for (FJTask det : t.dependents) {
					synchronized (det) {
						det.depCount--;
						if (det.depCount == 0) {
							tasksToRelease.add(det);
						}
					}
				}
			}
			
			// Release parent
			if (t.parent != null && t.parent != Runtime.NO_PARENT) {
				synchronized(t.parent) {
					t.parent.childCount--;
					if (t.parent.childCount == 0) {
						tasksToFinish.add(t.parent);
					}
				}
			}
			
			for (FJTask tr : tasksToRelease) {
				handleTaskGo(tr);
			}
			
			for (FJTask tr : tasksToFinish) {
				handleEndOfTask(tr);
			}
			
		}
	}

	@Override
	public void addErrorHandler(ErrorHandler eh) {
		er = eh;
	}

	@Override
	public void removeErrorHandler(ErrorHandler eh) {
		er = null;
	}

	public void receiveException(FJTask t, Exception e) {
		if (er != null) {
			er.handleTaskException(t, e);
		}
	}

}
