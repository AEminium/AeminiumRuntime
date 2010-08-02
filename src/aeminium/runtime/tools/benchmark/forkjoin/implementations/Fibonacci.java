package aeminium.runtime.tools.benchmark.forkjoin.implementations;

import jsr166y.*;

@SuppressWarnings("serial")
public

class Fibonacci extends RecursiveAction { 
	public volatile int number;
	// This value is the one used in Doug Lea's Paper on ForkJoin
	private int THREASHOLD = 13;
	private static int TARGET = 2;
	
	
	public Fibonacci(int n) { number = n; }

	private int seqFib(int n) {
		if (n <= 2) return 1;
		else return seqFib(n-1) + seqFib(n-2);
	}

	@Override
	protected void compute() {
		int n = number;
		if (n <= 1) { /* do nothing */ }
		else if (n <= THREASHOLD) 
			number = seqFib(n);
		else {
			Fibonacci f1 = new Fibonacci(n - 1);	
			Fibonacci f2 = new Fibonacci(n - 2);
			invokeAll(f1,f2);
			number = f1.number + f2.number; // compose
		}
	}
	
	public static void main(String[] args) {
		ForkJoinPool pool = new ForkJoinPool();
		Fibonacci t = new Fibonacci(TARGET);
		pool.invoke(t);
		System.out.println("Final result = " + t.number);
	}
}