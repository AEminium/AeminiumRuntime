package aeminiumruntime.tools.benchmark;

import aeminiumruntime.Body;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;
import aeminiumruntime.launcher.RuntimeFactory;
import aeminiumruntime.queue.QRuntime;

public class ChildTaskBenchmark implements IBenchmark {
	private final String name = "ChildTaskBenchmark";
	private int[] levels = {1, 2, 3, 4, 5, 6, 7, 8};
	private int fanout = 4;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run(IReporter reporter) {
		for (int level : levels) {
			runTest(reporter, level);
		}
	}
	
	public void runTest(IReporter reporter, int level) {
		//Runtime rt = new QRuntime();
		Runtime rt = RuntimeFactory.getRuntime(false);
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
		});
	}
}
