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

package aeminium.runtime.tools.benchmark.forkjoin;

import jsr166y.ForkJoinPool;
import aeminium.runtime.examples.fjtests.AeminiumMergeSort;
import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.MergeSort;

public class MergeSortBenchmarkSuite {
	
	Benchmark[] tests;
	
	private int sizeOfTests = 1000000;
	private int threshold = 10000;
	
	public MergeSortBenchmarkSuite() {
		tests = new Benchmark[3];
		
		tests[0] = new Benchmark() {
			@Override
			public String getName() {
				return "Sequential Sort";
			}
			
			@Override
			public long run() {
				long[] arrayToSort = MergeSort.generateRandomArray(sizeOfTests);
				MergeSort task = new MergeSort(arrayToSort, threshold);
				
				long start = System.nanoTime();
				task.sequentialSort();
				long end = System.nanoTime();
				
				return end-start;
			}
		};
		
		tests[1] = new Benchmark() {
			
			ForkJoinPool pool = new ForkJoinPool();
			@Override
			public String getName() {
				return "ForkJoin MergeSort";
			}
			
			@Override
			public long run() {
				long[] arrayToSort = MergeSort.generateRandomArray(sizeOfTests);
				
				MergeSort task = new MergeSort(arrayToSort, threshold);
				
				long start = System.nanoTime();
				pool.invoke(task);
				long end = System.nanoTime();
				
				return end-start;
			}
		};
		
		tests[2] = new Benchmark() {
			
			Runtime rt = Factory.getRuntime();
			
			@Override
			public String getName() {
				return "Aeminium MergeSort";
			}
			
			@Override
			public long run() {
				long[] arrayToSort = MergeSort.generateRandomArray(sizeOfTests);
				
				AeminiumMergeSort merger = new AeminiumMergeSort(arrayToSort, threshold);
				rt.init();
				long start = System.nanoTime();
				merger.doSort(rt);
				rt.shutdown();
				long end = System.nanoTime();
				
				return end-start;
			}
		};
		
	}
	
	
	public static void main(String[] args) {
		MergeSortBenchmarkSuite suite = new MergeSortBenchmarkSuite();
		new BenchmarkExecutor(suite.getTests()).run(args);
	}
	
	public Benchmark[] getTests() {
		return tests;
	}

}
