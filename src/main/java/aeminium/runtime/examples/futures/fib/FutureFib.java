package aeminium.runtime.examples.futures.fib;

import aeminium.runtime.futures.Future;
import aeminium.runtime.futures.RuntimeManager;

public class FutureFib {
	public static void main(String[] args) {
		RuntimeManager.init();
		FutureFib f = new FutureFib();
		System.out.println(f.fib(5));
		RuntimeManager.shutdown();
	}

	public int fib(int n) {
		if (n<2) return 1;
		else {
			Future<Integer> a1 = new Future<Integer>((t) -> fib(n-1));
			Future<Integer> a2 = new Future<Integer>((t) -> fib(n-2));
			return a1.get() + a2.get();
		}
	}

	int[] createArray() {
		int[] a = new int[1];
		a[0] = 10;
		return a;
	}

}
