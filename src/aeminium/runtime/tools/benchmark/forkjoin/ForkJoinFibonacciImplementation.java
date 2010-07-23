package aeminium.runtime.tools.benchmark.forkjoin;

import jsr166y.*;

@SuppressWarnings("serial")
public

class ForkJoinFibonacciImplementation extends RecursiveAction { 
	public volatile int number;
	// This value is the one used in Doug Lea's Paper on ForkJoin
	private int THREASHOLD = 13;
	private static int TARGET = 2;
	
	
	public ForkJoinFibonacciImplementation(int n) { number = n; }

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
			ForkJoinFibonacciImplementation f1 = new ForkJoinFibonacciImplementation(n - 1);	
			ForkJoinFibonacciImplementation f2 = new ForkJoinFibonacciImplementation(n - 2);
			invokeAll(f1,f2);
			number = f1.number + f2.number; // compose
		}
	}
	
	public static void main(String[] args) {
		ForkJoinPool pool = new ForkJoinPool();
		ForkJoinFibonacciImplementation t = new ForkJoinFibonacciImplementation(TARGET);
		pool.invoke(t);
		System.out.println("Final result = " + t.number);
	}
}