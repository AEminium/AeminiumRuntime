package aeminium.runtime.examples.fjtests;

import java.util.Random;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.BFS;

public class AeminiumBFS {

	public static class SearchBody implements Body {
		public volatile int value;
		private int threshold;
		private Graph graph;
		
		public SearchBody(int target, Graph graph, int threshold) {
			this.value = target;
			this.threshold = threshold;
			this.graph = graph;
		}
		
		@Override
		public void execute(Runtime rt, Task current) {
			if (BFS.probe(graph, threshold)) {
				value = BFS.seqCount(value, graph);
			} else {
				int found;
				if (value == graph.value) found = 1; else found = 0;
				Task[] tasks = new Task[graph.children.length];
				SearchBody[] bodies = new SearchBody[graph.children.length];
				for(int i=0;i<graph.children.length;i++){
					bodies[i] = new SearchBody(value, graph.children[i], threshold);
					tasks[i] = rt.createNonBlockingTask(bodies[i], Runtime.NO_HINTS);
					rt.schedule(tasks[i], current, Runtime.NO_DEPS);
				}
				for (int i=0; i<tasks.length;i++) {
					tasks[i].getResult();
					found += bodies[i].value;
				}
				value = found;
			}
			
			
		}
	}

	public static SearchBody createSearchBody(final Runtime rt, final int target, Graph graph, int threshold) {
		return new AeminiumBFS.SearchBody(target, graph, threshold);
	}

	public static void main(String[] args) {
		int target = 23;
		Runtime rt = Factory.getRuntime();
		rt.init();
		SearchBody body = createSearchBody(rt, 1, Graph.randomIntGraph(target, 2, new Random(1234567890)), 21);
		Task t1 = rt.createNonBlockingTask(body, Runtime.NO_HINTS);
		
		long start = System.nanoTime();
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.shutdown();
		long end = System.nanoTime();
		System.out.println("Found " + body.value + " occurrences of " + target
				+ " and took " + (end - start) + " nanoseconds.");
	}
}
