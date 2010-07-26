package aeminium.runtime.graph.implicit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import aeminium.runtime.CyclicDependencyError;
import aeminium.runtime.Runtime;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.Task;
import aeminium.runtime.graph.AbstractGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.task.TaskDescription;
import aeminium.runtime.task.implicit.ImplicitTask;
import aeminium.runtime.task.implicit.ImplicitTaskState;

public class ImplicitGraphBlockingQueue<T extends ImplicitTask> extends AbstractGraph<T> {
	private final List<T>  waitingForDeps= new LinkedList<T>();
	private final List<T>  running = new LinkedList<T>();
	private final List<T>  waitingForChildren = new LinkedList<T>();
	private final boolean checkForCycles;
	private final ImplicitTask[] ita = new ImplicitTask[0];
	private final ReentrantLock lockInternal = new ReentrantLock();
	private final LinkedBlockingQueue<T> pendingRequest = new LinkedBlockingQueue<T>();
	private final T END_TOKEN = (T) new AddTaskWrapper<T>(null, Runtime.NO_PARENT, null);
	private final T DEBUG_TOKEN = (T) new AddTaskWrapper<T>(null, Runtime.NO_PARENT, null);
	private  Thread workerThread;
	private Thread debugThread;
	private boolean exit_debug = false;
	private AtomicLong submittedCount = new AtomicLong();
	private AtomicLong finishedCount = new AtomicLong();
	
 	public ImplicitGraphBlockingQueue(RuntimePrioritizer<T> prioritizer, EnumSet<Flags> flags) {
		super(prioritizer, flags);
		if ( flags.contains(Flags.CHECK_FOR_CYCLES)) {
			checkForCycles = true;
		} else {
			checkForCycles = false;
		}
	}
	
	void debug() {
		while (true) {
			boolean wakeup_graph = false;
			boolean wakepup_request = false;
			
			if ( exit_debug ) return;
			if ( wakepup_request) {
				try {
					pendingRequest.put(DEBUG_TOKEN);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if ( wakeup_graph ) {
				synchronized (this) {
					this.notifyAll();
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	void workOnPendingRequests() {
		while (true) {
			T task;
			try {
				task = pendingRequest.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			if ( task == DEBUG_TOKEN ) continue;
			if ( task == END_TOKEN ) {
				return;
			}
			lockInternal.lock();
			if ( task instanceof AddTaskWrapper<?> ) {
				AddTaskWrapper<T> tw = (AddTaskWrapper<T>)task;
				addTaskInternal(tw.getTask(), tw.getParent(), tw.getDeps());
			} else {
				taskFinishedInternal(task);
			}
			lockInternal.unlock();
		}
	}
	
	@Override
	public void init() {
		workerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				workOnPendingRequests();
			}
		});
		workerThread.start();
		
		debugThread = new Thread(new Runnable() {
			@Override
			public void run() {
				debug();
			}
		});
		debugThread.start();
	}
	
	@Override
	public void shutdown() {
		try {
			pendingRequest.put(END_TOKEN);
			workerThread.join();
			exit_debug = true;
			debugThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void addTask(T task, Task parent, Collection<T> deps) {
		if ( lockInternal.tryLock() ) {
			addTaskInternal(task, parent, deps);
			lockInternal.unlock();
		} else {
			try {
				pendingRequest.put((T)new AddTaskWrapper<T>(task, parent, deps));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void addTaskInternal(T task, Task parent, Collection<T> deps) {
		synchronized (task) {
			// setup dependencies
			if ( task.getTaskState() != ImplicitTaskState.UNSCHEDULED ) {
				throw new RuntimeError("Task '" + task + "' has already been scheduled");
			}

			if ( deps != Runtime.NO_DEPS ) {
				task.setDependencies(new ArrayList<Task>(deps));
			} else {
				task.setDependencies(Runtime.NO_DEPS);
			}
			task.setParent(parent);
			if ( parent != Runtime.NO_PARENT ) {
				((T)parent).addChildTask(task);
				task.setParent(parent);
			}

			if ( checkForCycles ) {
				Collection<Task> taskDeps = Collections.unmodifiableList((List<? extends Task>) task.getDependencies());
				checkForCycles(task, taskDeps);
			}


			if ( task.getDependencies() == Runtime.NO_DEPS ) {
				running.add(task);
				task.setTaskState(ImplicitTaskState.RUNNING);
				prioritizer.scheduleTasks(task);
				submittedCount.addAndGet(1);
			} else {
				List<Task> doneTasks = new ArrayList<Task>();
				for ( Task t : task.getDependencies() ) {
					synchronized (t) {
						T at = (T)t;
						if ( at.getTaskState() != ImplicitTaskState.COMPLETED ) {
							at.addDependent(task);
						} else {
							doneTasks.add(at);
						}
					}
				}
				task.removeDependency(doneTasks);
				if ( task.getDependencies() != Runtime.NO_DEPS ){
					task.setTaskState(ImplicitTaskState.WAITING_FOR_DEPENDENCIES);
					waitingForDeps.add(task);
				} else {
					running.add(task);
					task.setTaskState(ImplicitTaskState.RUNNING);
					prioritizer.scheduleTasks(task);
					submittedCount.addAndGet(1);
				}
			}
		}
	}
	
	protected void checkForCycles(T task, Collection<Task> deps) {
		if ( deps == Runtime.NO_DEPS ) {
			return;
		}
		for ( Task t : deps ) {
			checkPath(task, (T)t);
		}
	}
	
	protected void checkPath(T task, T dep) {
		if ( task == dep ) {
			throw new CyclicDependencyError("Found Cycle for task: " + task);
		} else {
			Collection<Task> nextDeps;
			synchronized (dep) {
				 nextDeps = Collections.unmodifiableList((List<? extends Task>) dep.getDependencies());
			}
			checkForCycles(task, nextDeps);
		}
	}
	
	public void taskFinished(T task) {
		finishedCount.addAndGet(1);
		if ( lockInternal.tryLock() ) {
			taskFinishedInternal(task);
			lockInternal.unlock();
		} else {
			try {
				pendingRequest.put(task);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// task finished to run 
	protected void taskFinishedInternal(T task) {
		synchronized (task) {
			running.remove(task);
			if (task.hasChildren()) {
				waitingForChildren.add(task);
				task.setTaskState(ImplicitTaskState.WAITING_FOR_CHILDREN);
			} else {
				taskCompleted(task);
			}
		}
	}

	// have to synchronize on task and this
	protected void taskCompleted(ImplicitTask task) {
		synchronized (task) {
			if ( task.getTaskState() == ImplicitTaskState.WAITING_FOR_CHILDREN ) {
				waitingForChildren.remove(task);
			}
			task.setTaskState(ImplicitTaskState.COMPLETED);
			// callback 
			task.taskCompleted();
			
			if ( task.getParent() != Runtime.NO_PARENT ) {
				@SuppressWarnings("unchecked")
				T parent = (T)task.getParent();
				synchronized (parent) {
					parent.deleteChildTask(task);
					if ( !parent.hasChildren() && parent.getTaskState() == ImplicitTaskState.WAITING_FOR_CHILDREN) {
						taskCompleted(parent);
					}
				}
			}
			ArrayList<T> readyTasks = new ArrayList<T>(10);
			for ( Task t : task.getDependents() ) {
				synchronized (t) {
					@SuppressWarnings("unchecked")
					T at = (T)t;
					at.removeDependency(task);
					if ( at.getDependencies() == Runtime.NO_DEPS ) {
						waitingForDeps.remove(at);
						running.add(at);
						at.setTaskState(ImplicitTaskState.RUNNING);
						//prioritizer.scheduleTasks(at);
						readyTasks.add(at);
					}
				}
			}

			// trigger prioritize in case he was caching some tasks
			prioritizer.scheduleTasks((T[]) readyTasks.toArray(ita));
			submittedCount.addAndGet(readyTasks.size());

			// wake up waiting threads 
			if (waitingForChildren.isEmpty() && waitingForDeps.isEmpty() && running.isEmpty()  && pendingRequest.isEmpty() ) {
				synchronized (this) {
					//System.out.println("done!!!" + task);
					this.notifyAll();						
				}
			}
		}

	}
	
	public void waitToEmpty() {
		lockInternal.lock();
		while ( !(waitingForChildren.isEmpty() && waitingForDeps.isEmpty() && running.isEmpty()&& pendingRequest.isEmpty())) {
			try {
				lockInternal.unlock();
				synchronized (this) {
					this.wait();
				}
				lockInternal.lock();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lockInternal.unlock();
	}

	@Override
	public TaskDescription<T> getTaskDescription(T task) {
		return TaskDescription.create(task, task.getDependencies().size(), task.getDependents().size());
	}
}