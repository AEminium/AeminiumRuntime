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
		
		public abstract AbstractRuntime instanciate(EnumSet<Flags> flags);
	}
	
	@SuppressWarnings("unchecked")
	protected static final Map<String, ImplementationDeclaration> database = new HashMap<String, ImplementationDeclaration>();
	static {
		Factory f = new Factory();
		
		final ImplementationDeclaration<ImplicitTask> ImplicitGraphWithHybridThreadPoolSchedulerNoPrioritizer = f.new ImplementationDeclaration<ImplicitTask>("ImplicitGraphWithHybridThreadPoolSchedulerNoPrioritizer", "ImplicitGraphWithHybridThreadPoolSchedulerNoPrioritizer") {
			@Override
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				HybridThreadPoolsScheduler<ImplicitTask> scheduler = new HybridThreadPoolsScheduler<ImplicitTask>(flags);
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
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
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
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
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
		final ImplementationDeclaration<ImplicitTask> GenericGraphHybridThreadPoolSchedulerNoPrioritizer  = f.new ImplementationDeclaration<ImplicitTask>("GenericGraphHybridThreadPoolSchedulerNoPrioritizer", "GenericGraphHybridThreadPoolSchedulerNoPrioritizer") {
			@Override
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				HybridThreadPoolsScheduler<GenericTask> scheduler = new HybridThreadPoolsScheduler<GenericTask>(flags);
				GenericGraph<GenericTask> graph = new GenericGraph<GenericTask>(flags, scheduler);
				DataGroupFactory<GenericTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<GenericTask> taskFactory = GenericTask.createFactory(graph);
				return new GenericRuntime<GenericTask>(scheduler, 
															   scheduler, 
															   graph,
															   dgFactory,
															   taskFactory);
			}
		};
		database.put(GenericGraphHybridThreadPoolSchedulerNoPrioritizer.getName(), GenericGraphHybridThreadPoolSchedulerNoPrioritizer);
		
	
		//database.put("default", GenericGraphHybridThreadPoolSchedulerNoPrioritizer);
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
		return getRuntime("default", getFlagsFromEnvironment());
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
