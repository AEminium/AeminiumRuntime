package aeminiumruntime.examples;

import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;
import aeminiumruntime.implementations.Factory;

public class AtomicTest {
	
	private static int MAX_ITEMS = 100;
    
    public static void main(String[] args) {
        final Runtime rt = Factory.getRuntime();
        rt.init();
        
        
        final DataGroup d1 = rt.createDataGroup();
        
        for (int i = 0; i < MAX_ITEMS; i++) {
        	final int ii = i;
        	Task ti = rt.createAtomicTask(new Body() {
				@Override
				public void execute(Task current) {
					System.out.println("i:" + ii);
					for (int j=0; j < MAX_ITEMS/2; j++) {
						System.out.print(j + " ");
					}
					System.out.println(".");
				}
        	}, d1, Runtime.NO_HINTS);
        	rt.schedule(ti, Runtime.NO_PARENT, Runtime.NO_DEPS);
        }

        rt.shutdown();
    }
}
