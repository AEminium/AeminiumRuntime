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