package aeminium.runtime.implementations;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import aeminium.runtime.Runtime;
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
		
		public String getName() {
			return name;
		}
		
		public String getDescription() {
			return description;
		}
		
		public abstract AbstractRuntime instanciate(EnumSet<Flags> flags);
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
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				SingleFixedThreadPoolScheduler<ImplicitTask> scheduler = new SingleFixedThreadPoolScheduler<ImplicitTask>(flags);
				LowestLevelFirstPrioritizer<ImplicitTask> prioritizer = new LowestLevelFirstPrioritizer<ImplicitTask>(scheduler, flags);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(prioritizer, flags);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler, flags);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory(flags);
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
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				ForkJoinScheduler<ImplicitTask> scheduler = new ForkJoinScheduler<ImplicitTask>(flags);
				//LowestLevelFirstPrioritizer<ImplicitForkJoinTask> prioritizer = new LowestLevelFirstPrioritizer<ImplicitTask>(scheduler, flags);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler, flags);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler, flags);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory(flags);
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
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				PollingWorkStealingScheduler<ImplicitTask> scheduler = new PollingWorkStealingScheduler<ImplicitTask>(flags);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler, flags);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler, flags);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory(flags);
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
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				BlockingWorkStealingScheduler<ImplicitTask> scheduler = new BlockingWorkStealingScheduler<ImplicitTask>(flags);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler, flags);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler, flags);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory(flags);
				return new GenericRuntime<ImplicitTask>(scheduler, 
														scheduler, 
													    graph,
													    dgFactory,
													    taskFactory);
			}
		};
		database.put(ImplicitGraph_BlockingWorkStealingScheduler_None_FifoDataGroup.getName(), ImplicitGraph_BlockingWorkStealingScheduler_None_FifoDataGroup);

		// only export default implementation at the moment
		database.clear();
		
		// set default implementation
		database.put("default", ImplicitGraph_ForkJoinScheduler_None_FifoDataGroup);
		//database.put("default", ImplicitGraph_SingleThreadPoolScheduler_LowestLevelFirstPrioritizer_FifoDataGroup);
		//database.put("default", ImplicitGraph_PollingWorkStealingScheduler_None_FifoDataGroup);
		//database.put("default", ImplicitGraph_BlockingWorkStealingScheduler_None_FifoDataGroup);
	}
	
	/**
	 * Prohibit Factory instantiation.
	 */
	protected Factory() {} 
	
	@SuppressWarnings("unchecked")
	public final static Map<String, RuntimeConfiguration> getImplementations() {
		return Collections.unmodifiableMap(database);
	}
	
	/**
	 * Returns a new 'default' runtime object.
	 * @return
	 */
	public final static Runtime getRuntime() {
		return getRuntime(Configuration.getImplementation(), Configuration.getFlags());
	}
	
	/**
	 * Loads the implementation specified by 'name'. If the name is
	 * not found in the database then it assumes the name specifies the 
	 * class to load.
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final static Runtime getRuntime(String name, EnumSet<Flags> flags) {
		if ( database.containsKey(name)) {
			return database.get(name).instanciate(flags);
		} else {
			return getRuntime("default", flags);
		}
	}


}
