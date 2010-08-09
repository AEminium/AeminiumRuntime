package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

public final class Fibonacci {
	public final static int fibOf(int n) {
		if (n <= 2) {
			return 1;
		} else {
			return fibOf(n-1) + fibOf(n-2);
		}
	}
}
