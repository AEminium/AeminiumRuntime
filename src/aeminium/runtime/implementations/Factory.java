package aeminium.runtime.implementations;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import aeminium.runtime.Runtime;
import aeminium.runtime.datagroup.DataGroupFactory;
import aeminium.runtime.datagroup.fifo.FifoDataGroup;
import aeminium.runtime.graph.generic.GenericGraph;
import aeminium.runtime.graph.implicit.ImplicitGraph;
import aeminium.runtime.implementations.generic.GenericRuntime;
import aeminium.runtime.scheduler.forkjoin.ForkJoinScheduler;
import aeminium.runtime.scheduler.hybridthreadpools.HybridThreadPoolsScheduler;
import aeminium.runtime.scheduler.linear.LinearScheduler;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskFactory;
import aeminium.runtime.task.generic.GenericTask;
import aeminium.runtime.task.implicit.ImplicitTask;

public class Factory {
	protected static final String RT_PREFIX = "AEMINIUM_RT_";
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
		
		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_HybridThreadPoolsScheduler_None_ImplicitTask_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.HybridThreadPoolsScheduler.None.ImplicitTask.FifoDataGroup", "ImplicitGraph.HybridThreadPoolsScheduler.None.ImplicitTask.FifoDataGroup") {
			@Override
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				HybridThreadPoolsScheduler<ImplicitTask> scheduler = new HybridThreadPoolsScheduler<ImplicitTask>(flags);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler, flags);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler, flags);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory(graph, flags);
				return new GenericRuntime<ImplicitTask>(scheduler, 
															   scheduler, 
															   graph,
															   dgFactory,
															   taskFactory);
			}
		};
		database.put(ImplicitGraph_HybridThreadPoolsScheduler_None_ImplicitTask_FifoDataGroup.getName(), ImplicitGraph_HybridThreadPoolsScheduler_None_ImplicitTask_FifoDataGroup);

		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_ForkJoinScheduler_None_ImplicitTask_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.ForkJoinScheduler.None.ImplicitTask.FifoDataGroup", "ImplicitGraph.ForkJoinScheduler.None.ImplicitTask.FifoDataGroup") {
			@Override
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				ForkJoinScheduler<ImplicitTask> scheduler = new ForkJoinScheduler<ImplicitTask>(flags);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler, flags);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler, flags);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory(graph, flags);
				return new GenericRuntime<ImplicitTask>(scheduler, 
															   scheduler, 
															   graph,
															   dgFactory,
															   taskFactory);
			}
		};
		database.put(ImplicitGraph_ForkJoinScheduler_None_ImplicitTask_FifoDataGroup.getName(), ImplicitGraph_ForkJoinScheduler_None_ImplicitTask_FifoDataGroup);
		
		final RuntimeConfiguration<ImplicitTask> ImplicitGraphLinearSchedulerlNoPrioritizer = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.LinearScheduler.None.ImplicitTask.FifoDataGroup", "ImplicitGraph.LinearScheduler.None.ImplicitTask.FifoDataGroup") {
			@Override
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				LinearScheduler<ImplicitTask> scheduler = new LinearScheduler<ImplicitTask>(flags);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler, flags);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler, flags);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory(graph, flags);
				return new GenericRuntime<ImplicitTask>(scheduler, 
															   scheduler, 
															   graph,
															   dgFactory,
															   taskFactory);
			}
		};
		database.put(ImplicitGraphLinearSchedulerlNoPrioritizer.getName(), ImplicitGraphLinearSchedulerlNoPrioritizer);

		// TODO: still has some bugs
		final RuntimeConfiguration<ImplicitTask> GenericGraph_LinearScheduler_None_ImplicitTask_FifoDataGroup  = f.new RuntimeConfiguration<ImplicitTask>("GenericGraph.LinearScheduler.None.ImplicitTask.FifoDataGroup", "GenericGraph.LinearScheduler.None.ImplicitTask.FifoDataGroup") {
			@Override
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				HybridThreadPoolsScheduler<GenericTask> scheduler = new HybridThreadPoolsScheduler<GenericTask>(flags);
				GenericGraph<GenericTask> graph = new GenericGraph<GenericTask>( scheduler, flags);
				DataGroupFactory<GenericTask> dgFactory = FifoDataGroup.createFactory(scheduler, flags);
				TaskFactory<GenericTask> taskFactory = GenericTask.createFactory(graph, flags);
				return new GenericRuntime<GenericTask>(scheduler, 
															   scheduler, 
															   graph,
															   dgFactory,
															   taskFactory);
			}
		};
		database.put(GenericGraph_LinearScheduler_None_ImplicitTask_FifoDataGroup.getName(), GenericGraph_LinearScheduler_None_ImplicitTask_FifoDataGroup);
		
		database.put("default", ImplicitGraph_HybridThreadPoolsScheduler_None_ImplicitTask_FifoDataGroup);
	}
	
	/**
	 * Prohibit Factory instantiation.
	 */
	protected Factory() {} 
	
	@SuppressWarnings("unchecked")
	public static Map<String, RuntimeConfiguration> getImplementations() {
		return Collections.unmodifiableMap(database);
	}
	
	/**
	 * Returns a new 'default' runtime object.
	 * @return
	 */
	public static Runtime getRuntime() {
		return getRuntime(System.getenv(RT_PREFIX+"IMPLEMENTATION"), getFlagsFromEnvironment());
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
	public static Runtime getRuntime(String name, EnumSet<Flags> flags) {
		if ( database.containsKey(name)) {
			return database.get(name).instanciate(flags);
		} else {
			return getRuntime("default", flags);
		}
	}

	/**
	 * Computes flag set based on set environment variables.
	 * 
	 * @return
	 */
	public static EnumSet<Flags> getFlagsFromEnvironment() {
		EnumSet<Flags> flags = EnumSet.of(Flags.DEBUG);
		flags.clear();
		
		for ( Flags f : Flags.values() ) {
			Map<String, String> env = System.getenv();
			if ( env.containsKey(RT_PREFIX + f.name())) {
				flags.add(f);
			}
		}
				
		return flags;
	}

}
