package aeminiumruntime.examples;

import java.util.ArrayList;
import java.util.Collection;

import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;
import aeminiumruntime.Runtime;
import aeminiumruntime.Task;
import aeminiumruntime.launcher.RuntimeFactory;

public class AtomicTest {
    private static int MATRIX_SIZE = 4;
    private static int turn = 1;
    private static int[] score = {0,0};
    private static int[] count = {4,4};
    
    /* Object Store */
    private static int[][] matrix = new int[MATRIX_SIZE][MATRIX_SIZE];
    
    
    public static void printMatrix() {
    	for (int i1 = 0; i1 < MATRIX_SIZE; i1++) {
			for (int j1 = 0; j1 < MATRIX_SIZE; j1++) {
				System.out.print(matrix[i1][j1] + " ");
			}
			System.out.println();
		}
    	System.out.println(". . . . . . . .");
    }
    
    public static boolean mod2(int n) {
    	while (n >= 2) {
    		n-=2;
    	}
    	return (n==0);
    }
    
    public static void main(String[] args) {
        final Runtime rt = RuntimeFactory.getRuntime();
        rt.init();

        final Body b1 = new Body() {
            public void execute(Task parent) {
            	System.out.println("Defined matrix");
            	/* Sets the initial matrix */
            	for(int i=0; i < MATRIX_SIZE; i++) {
                	for(int j=0; j < MATRIX_SIZE; j++) {
                		if ( mod2(j + i) ) {
                			if (i < 2) matrix[i][j] = 1;
                			else matrix[i][j] = 2;
                		}
                	}   		
            	}
            }
        };
    
        
        final DataGroup d1 = rt.createDataGroup();
        
        Task t1 = rt.createBlockingTask(new Body() {
			
			@Override
			public void execute(Task parent) {
				b1.execute(parent);
				
			}
		}, Runtime.NO_HINTS);
        rt.schedule(t1, Runtime.NO_PARENT, null);
        
        final Collection<Task> deps1 = new ArrayList<Task>();       
        deps1.add(t1);
        

        Body manager = new Body() {
        	public void execute(Task parent) {
        			if ( count[turn] <= 0 ) return;
        			
        			printMatrix();
        		
        		    // Turn manager
        			final Collection<Task> waitFor = new ArrayList<Task>();
        			Task t2 = rt.createAtomicTask(new Body() {
        	        	public void execute(Task parent) {
        	        		int k,l;
        	        		int op = (turn==1) ? 2 : 1;
        	        		
        	        		k = (int)Math.round(Math.random() * MATRIX_SIZE);
        	        		l = (int)Math.round(Math.random() * MATRIX_SIZE);
        	        		
        	        		if (matrix[k][l] == op) {
        	        			score[turn]++;
        	        			count[op]--;
        	        		}
        	        		if (matrix[k][l] == turn) {
        	        			score[turn]--;
        	        			count[turn]--;
        	        		}
        	        		
        	        		// move piece
        	        		for(int i=0; i < MATRIX_SIZE; i++) {
        	                	for(int j=0; j < MATRIX_SIZE; j++) {
        	                		
        	                		int t,w;
        	                		if (op == 1) {
        	                			t=i;w=j;
        	                		} else {
        	                			t=MATRIX_SIZE-1-i;
        	                			w=MATRIX_SIZE-1-j;
        	                		}
        	                		
        	                		if (matrix[t][w] == turn) {
        	                			matrix[t][w]=0;
        	                			matrix[k][l]=turn;
        	                			return;
        	                		}
        	                	}
        	        		}
        	        	}
        			},d1, Runtime.NO_HINTS);
        			rt.schedule(t2, Runtime.NO_PARENT,null);
        			waitFor.add(t2);
        			rt.schedule( rt.createNonBlockingTask(this, Runtime.NO_HINTS), Runtime.NO_PARENT,waitFor);
        	}
        };
        
        Task t2 = rt.createNonBlockingTask(manager, Runtime.NO_HINTS);
        rt.schedule(t2, Runtime.NO_PARENT, deps1);
 
        rt.shutdown();
    }
}
