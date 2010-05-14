package aeminiumruntime.examples;

import java.util.ArrayList;
import java.util.Collection;

import aeminiumruntime.Runtime;
import aeminiumruntime.Task;
import aeminiumruntime.Body;
import aeminiumruntime.simpleparallel.ParallelRuntime;

public class Main {
    private static int MAX_CALC = 30;
     
    public static void main(String[] args) {
        final Runtime rt = new ParallelRuntime();
        rt.init();

        Body b1 = new Body() {
            public void execute() {
                int sum = 0;
                for (int i = 0; i < MAX_CALC; i++) {
                    sum +=i;
                }
                System.out.println("Sum: " + sum);
            }
        };

        Body b2 = new Body() {
            public void execute() {
                int max = 0;
                for (int i = 0; i < MAX_CALC; i++) {
                    if (i > max) max = i;
                    System.out.println("Calculating Maximum...");
                    try {
                        Thread.sleep(100);                        
                    } catch (InterruptedException e) {
                        // wait
                    }

                }
                System.out.println("Maximum: " + max);
            }
        };

        Body b3 = new Body() {
            public void execute() {
                for (int i = 0; i < MAX_CALC/5; i++) {
                    System.out.println("Processing...");
                }
            }
        };

        Task t1 = rt.createNonBlockingTask(b1);
        Task t2 = rt.createNonBlockingTask(b2);
        Task t3 = rt.createNonBlockingTask(b3);

        rt.schedule(t1, null);
        Collection<Task> deps = new ArrayList<Task>();
        deps.add(t1);
        rt.schedule(t2, deps);
        rt.schedule(t3, deps);
        rt.shutdown();
        
    }

}
