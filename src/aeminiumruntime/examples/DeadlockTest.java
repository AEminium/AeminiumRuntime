package aeminiumruntime.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import aeminiumruntime.Runtime;
import aeminiumruntime.Task;
import aeminiumruntime.Body;
import aeminiumruntime.launcher.RuntimeFactory;

public class DeadlockTest {
    
    public static void main(String[] args) {
        final Runtime rt = RuntimeFactory.getRuntime();
        rt.init();
        rt.startDebug(); /* required for cycle detection */
        
        Body b1 = new Body() {
            public void execute(Task parent) {
                System.out.println("Task 1");
            }
        };

        Body b2 = new Body() {
            public void execute(Task parent) {
                System.out.println("Task 2");
            }
        };
        
        Task t1 = rt.createNonBlockingTask(b1);
        Task t2 = rt.createNonBlockingTask(b2);
        Task t3= rt.createNonBlockingTask(b2);
        Task t4 = rt.createNonBlockingTask(b2);
        
        //ex: deps2 == task2 dependencies 
        Collection<Task> deps1 = new ArrayList<Task>();
        Collection<Task> deps2 = new ArrayList<Task>();
        Collection<Task> deps3 = new ArrayList<Task>();
        Collection<Task> deps4 = new ArrayList<Task>();
        
        deps1.add(t4);
        deps2.add(t1);
        deps3.add(t2);
        deps4.add(t3);
        
        
        Random r = new Random();
        if (r.nextBoolean()) {
	        rt.schedule(t1, Runtime.NO_PARENT, deps1); 
	        rt.schedule(t2, Runtime.NO_PARENT, deps2);
	        rt.schedule(t3, Runtime.NO_PARENT, deps3);
	        rt.schedule(t4, Runtime.NO_PARENT, deps4);
        } else {
        	// this also returns a deadlock
        	rt.schedule(t1, Runtime.NO_PARENT, deps2);
        }
        rt.shutdown();
    }
}
