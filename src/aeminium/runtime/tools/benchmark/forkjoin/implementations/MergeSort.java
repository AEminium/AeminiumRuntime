/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 * 
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

package aeminium.runtime.tools.benchmark.forkjoin.implementations;

import java.util.Arrays;
import java.util.Random;

import jsr166y.*;

@SuppressWarnings("serial")
public class MergeSort extends RecursiveAction {

	final long[] array;
	final long[] tmp;
	final int lo;
	final int hi;
	final int threshold;

	public MergeSort(long[] array) {
		this(array, new long[array.length],0, array.length, array.length/(Runtime.getRuntime().availableProcessors())+1);
	}
	
	public MergeSort(long[] array, int threshold) {
		this(array, new long[array.length],0, array.length, threshold);
	}
	
	public MergeSort(long[] array, long[] tmp, int lo, int hi, int thre) {
		this.array = array;
		this.tmp = tmp;
		this.lo = lo;
		this.hi = hi;
		this.threshold = thre;
	}

	@Override
	protected void compute() {
		if (hi - lo < threshold)
			quickSort(array, lo, hi);
		else {
			int mid = (lo + hi) >>> 1;
			RecursiveAction m1 = new MergeSort(array, tmp, lo, mid, threshold);
			RecursiveAction m2 = new MergeSort(array, tmp, mid+1, hi, threshold);

			invokeAll(m1, m2);
			merge(array, lo, mid, hi);
		}

	}

	private void merge(long[] whole, int lo, int mid, int hi) {
		// Code adapted from: http://blog.quibb.org/2010/03/jsr-166-the-java-forkjoin-framework/
		
		if ( whole[mid] <= whole[mid+1] ) {
			return;
		}
		System.arraycopy(whole, lo, tmp, lo, mid-lo+1);
		
		int i = lo, k=lo;
		int j = mid+1;
		
		while (k < j && j < hi) {
			if (tmp[i] <= whole[j]) {
				whole[k++] = tmp[i++];
			}  else {
				whole[k++] = whole[j++];
			}
		}
		System.arraycopy(tmp, i, whole, k, j-k);
		
		
	}

	public void sequentialSort() {
		sequentialSort(0, array.length);
	}
	
	public void sequentialSort(int lo, int hi) {
		if (hi - lo < threshold)
			quickSort(array, lo, hi);
		else {
			int mid = (lo + hi) >>> 1;
			sequentialSort(lo, mid);
			sequentialSort(mid+1, hi);
			merge(array, lo, mid, hi);
		}
	}
	
	public void quickSort(long[] array2, int lo2, int hi2) {
		Arrays.sort(array2, lo2, hi2);
	}
	
	public static void main(String[] args) {
		ForkJoinPool pool = new ForkJoinPool();
		long[] original = generateRandomArray(100000);
		MergeSort t = new MergeSort(original);
		pool.invoke(t);
		System.out.println("Sorted: " + checkArray(t.array));
		System.out.println("Array:" + Arrays.toString(t.array));
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
