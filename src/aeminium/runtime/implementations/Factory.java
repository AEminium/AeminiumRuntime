package aeminium.runtime.implementations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import aeminium.runtime.Runtime;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.datagroup.DataGroupFactory;
import aeminium.runtime.datagroup.fifo.FifoDataGroup;
import aeminium.runtime.graph.implicit.ImplicitGraph;
import aeminium.runtime.implementations.generic.GenericRuntime;
import aeminium.runtime.prioritizer.lowestlevelfirst.LowestLevelFirstPrioritizer;
import aeminium.runtime.scheduler.forkjoin.ForkJoinScheduler;
import aeminium.runtime.scheduler.singlethreadpool.fixed.SingleFixedThreadPoolScheduler;
import aeminium.runtime.scheduler.workstealing.blocking.BlockingWorkStealingScheduler;
import aeminium.runtime.scheduler.workstealing.polling.PollingWorkStealingScheduler;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskFactory;
import aeminium.runtime.task.implicit.ImplicitTask;

public class Factory {
	
	public abstract class RuntimeConfiguration<T extends RuntimeTask> {
		protected final String name;
		protected final String description;
		
		public RuntimeConfiguration(String name, String description) {
			this.name=name;
			this.description = description;
		}
		
		public final String getName() {
			return name;
		}
		
		public final String getDescription() {
			return description;
		}
		
		public abstract AbstractRuntime instanciate();
	}
	
	@SuppressWarnings("unchecked")
	protected static final Map<String, RuntimeConfiguration> database = new HashMap<String, RuntimeConfiguration>();
	
	static {
		/**
		 * Format for string database
		 * GraphClass.SchedulerClass.PrioritizerClass.TaskFactoryClass.DataGroupFactoryClass
		 */
		Factory f = new Factory();

		@SuppressWarnings("unchecked")
		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_SingleThreadPoolScheduler_LowestLevelFirstPrioritizer_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.SingleThreadPoolScheduler.LowestLevelFirstPrioritizer.ImplicitTask.FifoDataGroup", "ImplicitGraph.SingleThreadPoolScheduler.LowestLevelFirstPrioritizer.ImplicitTask.FifoDataGroup") {
			@Override
			public final AbstractRuntime instanciate() {
				SingleFixedThreadPoolScheduler<ImplicitTask> scheduler = new SingleFixedThreadPoolScheduler<ImplicitTask>();
				LowestLevelFirstPrioritizer<ImplicitTask> prioritizer = new LowestLevelFirstPrioritizer<ImplicitTask>(scheduler);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(prioritizer);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory();
				return new GenericRuntime<ImplicitTask>(scheduler, 
														 prioritizer, 
													     graph,
													     dgFactory,
													     taskFactory);
			}
		};
		database.put(ImplicitGraph_SingleThreadPoolScheduler_LowestLevelFirstPrioritizer_FifoDataGroup.getName(), ImplicitGraph_SingleThreadPoolScheduler_LowestLevelFirstPrioritizer_FifoDataGroup);

		@SuppressWarnings("unchecked")
		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_ForkJoinScheduler_None_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.ForkJoinScheduler.None.ImplicitTask.FifoDataGroup", "ImplicitGraph.ForkJoinScheduler.None.ImplicitTask.FifoDataGroup") {
			@Override
			public final AbstractRuntime instanciate() {
				ForkJoinScheduler<ImplicitTask> scheduler = new ForkJoinScheduler<ImplicitTask>();
				//LowestLevelFirstPrioritizer<ImplicitForkJoinTask> prioritizer = new LowestLevelFirstPrioritizer<ImplicitTask>(scheduler, flags);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory();
				return new GenericRuntime<ImplicitTask>(scheduler, 
														scheduler, 
													    graph,
													    dgFactory,
													    taskFactory);
			}
		};
		database.put(ImplicitGraph_ForkJoinScheduler_None_FifoDataGroup.getName(), ImplicitGraph_ForkJoinScheduler_None_FifoDataGroup);

		@SuppressWarnings("unchecked")
		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_PollingWorkStealingScheduler_None_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.PollingWorkStealingScheduler.None.FifoDataGroup", "ImplicitGraph.PollingWorkStealingScheduler.None.FifoDataGroup") {
			@Override
			public final AbstractRuntime instanciate() {
				PollingWorkStealingScheduler<ImplicitTask> scheduler = new PollingWorkStealingScheduler<ImplicitTask>();
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory();
				return new GenericRuntime<ImplicitTask>(scheduler, 
														scheduler, 
													    graph,
													    dgFactory,
													    taskFactory);
			}
		};
		database.put(ImplicitGraph_PollingWorkStealingScheduler_None_FifoDataGroup.getName(), ImplicitGraph_PollingWorkStealingScheduler_None_FifoDataGroup);

		@SuppressWarnings("unchecked")
		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_BlockingWorkStealingScheduler_None_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.BlockingWorkStealingScheduler.None.FifoDataGroup", "ImplicitGraph.BlockingWorkStealingScheduler.None.FifoDataGroup") {
			@Override
			public final AbstractRuntime instanciate() {
				BlockingWorkStealingScheduler<ImplicitTask> scheduler = new BlockingWorkStealingScheduler<ImplicitTask>();
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory();
				return new GenericRuntime<ImplicitTask>(scheduler, 
														scheduler, 
													    graph,
													    dgFactory,
													    taskFactory);
			}
		};
		database.put(ImplicitGraph_BlockingWorkStealingScheduler_None_FifoDataGroup.getName(), ImplicitGraph_BlockingWorkStealingScheduler_None_FifoDataGroup);
		
		// set default implementation
		//database.put("default", ImplicitGraph_ForkJoinScheduler_None_FifoDataGroup);
		//database.put("default", ImplicitGraph_SingleThreadPoolScheduler_LowestLevelFirstPrioritizer_FifoDataGroup);
		//database.put("default", ImplicitGraph_PollingWorkStealingScheduler_None_FifoDataGroup);
		database.put("default", ImplicitGraph_BlockingWorkStealingScheduler_None_FifoDataGroup);
	
		if ( !database.containsKey("default")) {
			throw new RuntimeError("Implementation database does not contain a 'default' implementation.");
		}
	}
	
	/**
	 * Prohibit Factory instantiation.
	 */
	protected Factory() {} 
	
	@SuppressWarnings("unchecked")
	protected final static Map<String, RuntimeConfiguration> getImplementations() {
		return Collections.unmodifiableMap(database);
	}
	
	/**
	 * Returns a new 'default' runtime object.
	 * @return
	 */
	public final static Runtime getRuntime() {
		return getRuntime(Configuration.getImplementation());
	}
	
	/**
	 * Loads the implementation specified by 'name'. If the name is
	 * not found in the database then it assumes the name specifies the 
	 * class to load.
	 * 
	 * @param name
	 * @return
	 */
	protected final static Runtime getRuntime(String name) {
		if ( database.containsKey(name)) {
			return database.get(name).instanciate();
		} else {
			return getRuntime("default");
		}
	}


}
