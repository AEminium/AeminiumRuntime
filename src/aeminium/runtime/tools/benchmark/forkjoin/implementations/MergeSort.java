package aeminium.runtime.tools.benchmark.forkjoin.implementations;

import java.util.Arrays;
import java.util.Random;

import jsr166y.*;

@SuppressWarnings("serial")
public class MergeSort extends RecursiveAction {

	int THRESHOLD = 1000;

	final long[] array;
	final int lo;
	final int hi;

	public MergeSort(long[] array) {
		this(array, 0, array.length);
	}
	
	public MergeSort(long[] array, int lo, int hi) {
		this.array = array;
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	protected void compute() {
		if (hi - lo < THRESHOLD)
			sequentiallySort(array, lo, hi);
		else {
			int mid = (lo + hi) >>> 1;
			RecursiveAction m1 = new MergeSort(array, lo, mid);
			RecursiveAction m2 = new MergeSort(array, mid, hi);

			invokeAll(m1, m2);
			merge(array, lo, mid, hi);
		}

	}

	private void merge(long[] whole, int lo, int mid, int hi) {		
		
		int leftIndex = 0;
		int rightIndex = 0;
		int wholeIndex = 0;

		long[] left = Arrays.copyOf(whole, mid);
		long[] right = Arrays.copyOfRange(whole, mid, hi);

		// As long as neither the left nor the right array has
		// been used up, keep taking the smaller of left[leftIndex]
		// or right[rightIndex] and adding it at both[bothIndex].
		while (leftIndex < left.length && rightIndex < right.length) {
			if (left[leftIndex] < right[rightIndex]) {
				whole[wholeIndex] = left[leftIndex];
				leftIndex++;
			} else {
				whole[wholeIndex] = right[rightIndex];
				rightIndex++;
			}
			wholeIndex++;
		}

		long[] rest;
		int restIndex;
		if (leftIndex >= left.length) {
			// The left array has been use up...
			rest = right;
			restIndex = rightIndex;
		} else {
			// The right array has been used up...
			rest = left;
			restIndex = leftIndex;
		}

		// Copy the rest of whichever array (left or right) was
		// not used up.
		for (int i = restIndex; i < rest.length; i++) {
			whole[wholeIndex] = rest[i];
			wholeIndex++;
		}
	}

	public static void sequentialSort(long[] array) {
		Arrays.sort(array);
	}
	
	public void sequentiallySort(long[] array2, int lo2, int hi2) {
		Arrays.sort(array2);
	}
	
	public static void main(String[] args) {
		ForkJoinPool pool = new ForkJoinPool();
		long[] original = generateRandomArray(1000);
		MergeSort t = new MergeSort(original, 0, original.length);
		pool.invoke(t);
		System.out.println("Sorted: " + checkArray(t.array));
		System.out.println("Final result = " + Arrays.toString(t.array));
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
		long[] ar = new long[size];
		for (int i=0; i<size; i++) {
			if (i % 123 == 0) r.setSeed(1241431423453252L + i);
			ar[i] = r.nextLong();
		}
		return ar;
	}

}
