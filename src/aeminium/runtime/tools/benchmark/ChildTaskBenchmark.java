package aeminium.runtime.tools.benchmark;

import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;

public class ChildTaskBenchmark implements Benchmark {
	private final String name = "ChildTaskBenchmark";
	private int[] levels = {1, 2, 3, 4, 5, 6, 7, 8};
	private int fanout = 4;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		for (int level : levels) {
			runTest(version, flags, reporter, level);
			reporter.flush();
		}
	}
	
	public void runTest(String version, EnumSet<Flags> flags, Reporter reporter, int level) {
		Runtime rt = Factory.getRuntime(version, flags);
			long start = System.nanoTime();
			rt.init();
			
			Task root = creatTaskWithChildren(rt, level, fanout);
			rt.schedule(root, Runtime.NO_PARENT, Runtime.NO_DEPS);
			
			rt.shutdown();		
			long end = System.nanoTime();
			long count = (long)Math.pow(fanout, level)+1;
			String result = String.format("Level %3d with %6d tasks in %12d ns ==> %10d ns per task | %6d tasks/second. ", level, count, (end-start), ((end-start)/count),  (1000000000/((end-start)/count)));
			reporter.reportLn(result);

	}

	public Task creatTaskWithChildren(final Runtime rt, final int level, final int fanout) {
		return rt.createNonBlockingTask(new Body() {
			
			@Override
			public void execute(Task current) {
				if ( 0 < level ) {
					for ( int i = 0; i < fanout; i++ ) {
						Task childTask = creatTaskWithChildren(rt, level-1, fanout);
						rt.schedule(childTask, current, Runtime.NO_DEPS);
					}
				}
			}
		}, Runtime.NO_HINTS);
	}
}
