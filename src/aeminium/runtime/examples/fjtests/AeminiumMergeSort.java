package aeminium.runtime.examples.fjtests;

import java.util.Arrays;
import java.util.Random;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class AeminiumMergeSort {
	final long[] array;
	final long[] tmp;
	int threshold;
	
	public AeminiumMergeSort(long[] array) {
		this(array, array.length/(java.lang.Runtime.getRuntime().availableProcessors() + 1));
	}
	
	public AeminiumMergeSort(long[] array, int threshold) {
		this.array = array;
		this.tmp = new long[array.length];
		this.threshold = threshold;
	}
	
	public void doSort(Runtime rt) {
		Task main = createSorter(rt, 0, array.length);
		rt.schedule(main, Runtime.NO_PARENT, Runtime.NO_DEPS);
	}
	
	
	public Task createSorter(Runtime rt, final int lo, final int hi) {
		Task main = rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task current) {
				if ( hi-lo < threshold) {
					Arrays.sort(array, lo, hi);
				} else {
					int mid = (lo+hi) >>> 1;
					Task t1 = createSorter(rt, lo, mid);
					rt.schedule(t1, current, Runtime.NO_DEPS);
					Task t2 = createSorter(rt, mid+1, hi);
					rt.schedule(t2, current, Runtime.NO_DEPS);
					
					Task t3 = createMerger(rt, lo, mid, hi);
					rt.schedule(t3, current, Arrays.asList(t1, t2));
				}
				
			}
			
		}, Runtime.NO_HINTS);
		return main;
	}
	
	
	private Task createMerger(Runtime rt, final int lo, final int mid, final int hi) {
		Task merger = rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task current) {
				if ( array[mid] <= array[mid+1] ) {
					return;
				}
				System.arraycopy(array, lo, tmp, lo, mid-lo+1);
				
				int i = lo, k=lo;
				int j = mid+1;
				
				while (k < j && j <= hi) {
					if (tmp[i] <= array[j]) {
						array[k++] = tmp[i++];
					}  else {
						array[k++] = array[j++];
					}
				}
				System.arraycopy(tmp, i, array, k, j-k);
				
			}
			
		}, Runtime.NO_HINTS);
		return merger;
		
	}
	
	/* Auxiliary Stuff for standalone running */
	
	public static void main(String ...args) {
		
		long[] original = generateRandomArray(10);
		AeminiumMergeSort merger = new AeminiumMergeSort(original);
		
		Runtime rt = Factory.getRuntime();
		rt.init();
		merger.doSort(rt);
		rt.shutdown();
		System.out.println("Sorted: " + checkArray(merger.array));
		System.out.println("Array: " + Arrays.toString(merger.array));
	}
	
	public static boolean checkArray(long[] c) {
		boolean st = true;
		for (int i=0; i<c.length-1; i++) {
			st = st && (c[i] <= c[i+1]);
		}
		return st;
	}
	
	public static long[] generateRandomArray(int size) {
		Random r = new Random();
		r.setSeed(1234567890);
		long[] ar = new long[size];
		for (int i=0; i<size; i++) {
			ar[i] = r.nextLong();
		}
		return ar;
	}
	
}
