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

import jsr166y.*;

@SuppressWarnings("serial")
public

class Fibonacci extends RecursiveAction { 
	public volatile int number;
	private int threshold = 13;
	
	public Fibonacci(int n, int thre) { 
		number = n;
		threshold = thre;
	}

	private int seqFib(int n) {
		if (n <= 2) return 1;
		else return seqFib(n-1) + seqFib(n-2);
	}

	@Override
	protected void compute() {
		int n = number;
		if (n <= 1) { /* do nothing */ }
		else if (n <= threshold) 
			number = seqFib(n);
		else {
			Fibonacci f1 = new Fibonacci(n - 1, threshold);	
			Fibonacci f2 = new Fibonacci(n - 2, threshold);
			invokeAll(f1,f2);
			number = f1.number + f2.number; // compose
		}
	}
	
	public static void main(String[] args) {
		ForkJoinPool pool = new ForkJoinPool();
		Fibonacci t = new Fibonacci(14, 2);
		pool.invoke(t);
		System.out.println("Final result = " + t.number);
	}
}