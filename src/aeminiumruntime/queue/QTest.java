package aeminiumruntime.queue;

import java.util.Arrays;

import aeminiumruntime.Body;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;

public class QTest {
	public static void main(String[] args) {
		final Runtime rt = new QRuntime();
		
		rt.init();
		
		Task t1 = rt.createNonBlockingTask(new Body() {
			@Override
			public void execute(Task current) {
				System.out.println("I'm t1");
				Task t11 = rt.createNonBlockingTask(new Body() {
					@Override
					public void execute(Task parent) {
						System.out.println("I'm 1.1");
					}
					@Override
					public String toString() {return "t1.1";}
				});
				rt.schedule(t11, current, aeminiumruntime.Runtime.NO_DEPS);
				Task t12 = rt.createNonBlockingTask(new Body() {
					@Override
					public void execute(Task parent) {
						System.out.println("I'm 1.2");
					}
					@Override
					public String toString() {return "t1.2";}
				});
				rt.schedule(t12, current, aeminiumruntime.Runtime.NO_DEPS);
				Task t13 = rt.createNonBlockingTask(new Body() {
					@Override
					public void execute(Task parent) {
						System.out.println("I'm 1.3");
					}
					@Override
					public String toString() {return "t1.3";}
				});
				rt.schedule(t13, current, aeminiumruntime.Runtime.NO_DEPS);
			}
			@Override
			public String toString() {return "t1";}
		});
		
		Task t2 = rt.createNonBlockingTask(new Body() {
			@Override
			public void execute(Task parent) {
				System.out.println("I'm t2");				
			}
			@Override
			public String toString() {return "t2";}

		});
		
		rt.schedule(t2, Runtime.NO_PARENT, Arrays.asList(t1));
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		
		rt.shutdown();
	}
}
