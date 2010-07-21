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
	public abstract class ImplementationDeclaration<T extends RuntimeTask> {
		protected final String name;
		protected final String description;
		
		public ImplementationDeclaration(String name, String description) {
			this.name=name;
			this.description = description;
		}
		
		public String getName() {
			return name;
		}
		
		public String getDescription() {
			return description;
		}
		
		public abstract AbstractRuntime instanciate(EnumSet<Flag> flags);
	}
	
	@SuppressWarnings("unchecked")
	protected static final Map<String, ImplementationDeclaration> database = new HashMap<String, ImplementationDeclaration>();
	static {
		Factory f = new Factory();
		
		final ImplementationDeclaration<ImplicitTask> ImplicitGraphWithHybridThreadPoolSchedulerNoPrioritizer = f.new ImplementationDeclaration<ImplicitTask>("ImplicitGraphWithHybridThreadPoolSchedulerNoPrioritizer", "ImplicitGraphWithHybridThreadPoolSchedulerNoPrioritizer") {
			@Override
			public AbstractRuntime instanciate(EnumSet<Flag> flags) {
				HybridThreadPoolsScheduler<ImplicitTask> scheduler = new HybridThreadPoolsScheduler<ImplicitTask>();
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(flags, scheduler);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory(graph);
				return new GenericRuntime<ImplicitTask>(scheduler, 
															   scheduler, 
															   graph,
															   dgFactory,
															   taskFactory);
			}
		};
		database.put(ImplicitGraphWithHybridThreadPoolSchedulerNoPrioritizer.getName(), ImplicitGraphWithHybridThreadPoolSchedulerNoPrioritizer);

		final ImplementationDeclaration<ImplicitTask> ImplicitGraphWithForkJoinPoolSchedulerNoPrioritizer = f.new ImplementationDeclaration<ImplicitTask>("ImplicitGraphWithForkJoinPoolSchedulerNoPrioritizer", "ImplicitGraphWithForkJoinPoolSchedulerNoPrioritizer") {
			@Override
			public AbstractRuntime instanciate(EnumSet<Flag> flags) {
				ForkJoinScheduler<ImplicitTask> scheduler = new ForkJoinScheduler<ImplicitTask>(flags);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(flags, scheduler);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory(graph);
				return new GenericRuntime<ImplicitTask>(scheduler, 
															   scheduler, 
															   graph,
															   dgFactory,
															   taskFactory);
			}
		};
		database.put(ImplicitGraphWithForkJoinPoolSchedulerNoPrioritizer.getName(), ImplicitGraphWithForkJoinPoolSchedulerNoPrioritizer);
		
		final ImplementationDeclaration<ImplicitTask> ImplicitGraphLinearSchedulerlNoPrioritizer = f.new ImplementationDeclaration<ImplicitTask>("ImplicitGraphLinearSchedulerlNoPrioritizer", "ImplicitGraphLinearSchedulerlNoPrioritizer") {
			@Override
			public AbstractRuntime instanciate(EnumSet<Flag> flags) {
				LinearScheduler<ImplicitTask> scheduler = new LinearScheduler<ImplicitTask>(flags);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(flags, scheduler);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory(graph);
				return new GenericRuntime<ImplicitTask>(scheduler, 
															   scheduler, 
															   graph,
															   dgFactory,
															   taskFactory);
			}
		};
		database.put(ImplicitGraphLinearSchedulerlNoPrioritizer.getName(), ImplicitGraphLinearSchedulerlNoPrioritizer);

		// TODO: still has some bugs
//		final ImplementationDeclaration<ImplicitTask> GenericGraphLinearSchedulerlNoPrioritizer = f.new ImplementationDeclaration<ImplicitTask>("GenericGraphLinearSchedulerlNoPrioritizer", "GenericGraphLinearSchedulerlNoPrioritizer") {
//			@Override
//			public AbstractRuntime instanciate(EnumSet<Flag> flags) {
//				LinearScheduler<RuntimeTask> scheduler = new LinearScheduler<RuntimeTask>(flags);
//				GenericGraph<RuntimeTask> graph = new GenericGraph<RuntimeTask>(flags, scheduler);
//				DataGroupFactory<RuntimeTask> dgFactory = FifoDataGroup.createFactory(scheduler);
//				TaskFactory<RuntimeTask> taskFactory = GenericTask.createFactory(graph);
//				return new GenericRuntime<RuntimeTask>(scheduler, 
//															   scheduler, 
//															   graph,
//															   dgFactory,
//															   taskFactory);
//			}
//		};
//		database.put(GenericGraphLinearSchedulerlNoPrioritizer.getName(), GenericGraphLinearSchedulerlNoPrioritizer);
		
	
		database.put("default", ImplicitGraphWithHybridThreadPoolSchedulerNoPrioritizer);
	}
	
	/**
	 * Prohibit Factory instantiation.
	 */
	protected Factory() {} 
	
	@SuppressWarnings("unchecked")
	public static Map<String, ImplementationDeclaration> getImplementations() {
		return Collections.unmodifiableMap(database);
	}
	
	/**
	 * Returns a new 'default' runtime object.
	 * @return
	 */
	public static Runtime getRuntime() {
		EnumSet<Flag> flags =  EnumSet.of(Flag.DEBUG);
		flags.clear();
		return getRuntime("default", flags);
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
	public static Runtime getRuntime(String name, EnumSet<Flag> flags) {
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
	public static EnumSet<Flag> getFlagsFromEnvironment() {
		EnumSet<Flag> flags = EnumSet.of(Flag.DEBUG);
		flags.clear();
		
		for ( Flag f : Flag.values() ) {
			Map<String, String> env = System.getenv();
			if ( env.containsKey(RT_PREFIX + f.name())) {
				flags.add(f);
			}
		}
				
		return flags;
	}
	
}
