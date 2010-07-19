package aeminiumruntime.launcher;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import aeminiumruntime.Runtime;
import aeminiumruntime.graphs.ParallelTaskGraph;
import aeminiumruntime.graphs.TaskGraph;
import aeminiumruntime.linear.LinearRuntime;
import aeminiumruntime.prioritizers.AdversialPrioritizer;
import aeminiumruntime.prioritizers.LinearPrioritizer;
import aeminiumruntime.prioritizers.Prioritizer;
import aeminiumruntime.queue.QRuntime;
import aeminiumruntime.schedulers.EagerParallelScheduler;
import aeminiumruntime.schedulers.ForkJoinScheduler;
import aeminiumruntime.schedulers.HybridForkJoinScheduler;
import aeminiumruntime.schedulers.LinearScheduler;
import aeminiumruntime.schedulers.ParallelScheduler;
import aeminiumruntime.schedulers.Scheduler;
import aeminiumruntime.simpleparallel.ParallelRuntime;

public class RuntimeFactory {
	
	public static int PRI_LINEAR = 0;
	public static int PRI_SMART = 1;
	public static int PRI_ADVERSIAL = 2;
	
	public static int SCH_LINEAR = 0;
	public static int SCH_EAGER = 1;
	public static int SCH_PARALLEL = 2;
	public static int SCH_FORKJOIN = 3;
	public static int SCH_HYBRID = 4;
	
	public static Runtime getLinearRuntime() {
		return new LinearRuntime();
	}

	public static Runtime getRuntime(boolean debug) {
		return new ParallelRuntime(debug);
	}
	
	public static Runtime getQueueRuntime() {
		return new QRuntime();
	}
	
	public static Runtime getRuntime() {
		return getParallelRuntime(false, SchedulerType.HYBRID, PrioritizerType.SMART);
	}
	
	public static Runtime getDebugRuntime() {
		return getParallelRuntime(true, SchedulerType.PARALLEL, PrioritizerType.SMART);
	}
	
	/* Dynamic loading, but static one should be faster */
	public static Runtime getParallelRuntime(boolean debug, TaskGraph graph, Class<Scheduler> schedulerClass) {
		ParallelRuntime rt = (ParallelRuntime) getRuntime(debug);
		rt.setGraph(graph);
		try {
			rt.setScheduler((Scheduler) schedulerClass.getConstructor(graph.getClass()).newInstance(graph) );
		} catch (IllegalArgumentException e) {
			Logger.getLogger(RuntimeFactory.class.getName()).log(Level.SEVERE, "Illegal Scheduler. Should accept a graph as first argument in Constructor.", e);
		} catch (SecurityException e) {
			Logger.getLogger(RuntimeFactory.class.getName()).log(Level.SEVERE, "Illegal Scheduler", e);
		} catch (InstantiationException e) {
			Logger.getLogger(RuntimeFactory.class.getName()).log(Level.SEVERE, "Illegal Scheduler", e);
		} catch (IllegalAccessException e) {
			Logger.getLogger(RuntimeFactory.class.getName()).log(Level.SEVERE, "Illegal Scheduler", e);
		} catch (InvocationTargetException e) {
			Logger.getLogger(RuntimeFactory.class.getName()).log(Level.SEVERE, "Illegal Scheduler", e);
		} catch (NoSuchMethodException e) {
			Logger.getLogger(RuntimeFactory.class.getName()).log(Level.SEVERE, "Illegal Scheduler", e);
		}
	
		return rt;
	}

	public static Runtime getParallelRuntime(boolean debug, SchedulerType schedulerMode, PrioritizerType prioritizerMode) {
		ParallelRuntime rt = new ParallelRuntime(debug);
		Prioritizer p;
		switch (prioritizerMode) {
			case LINEAR:
				p = new LinearPrioritizer();
				break;
			case ADVERSIAL:
				p = new AdversialPrioritizer();
				break;
			default:
				/* Null uses SmartPriotizer with graph as first argument*/
				p = null;
		}
		
		TaskGraph graph = new ParallelTaskGraph(p, debug);
		rt.setGraph(graph);
		
		Scheduler s;
		
		switch(schedulerMode) {
			case LINEAR:
				s = new LinearScheduler(graph);
				break;
			case EAGER:
				s = new EagerParallelScheduler(graph);
				break;
			case FORKJOIN:
				s = new ForkJoinScheduler(graph);
				break;
			case HYBRID:
				s = new HybridForkJoinScheduler(graph);
				break;
			default:
				s = new ParallelScheduler(graph);
		}
		
		rt.setScheduler(s);
		return rt;
	}
}
