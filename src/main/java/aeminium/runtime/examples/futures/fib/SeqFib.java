package aeminium.runtime.examples.futures.fib;


public class SeqFib {
	public static void main(String[] args) {
		SeqFib f = new SeqFib();
		System.out.println(f.fib(5));
	}

	public int fib(int n) {
		if (n<2) return 1;
		else return fib(n-1) + fib(n-2);
	}

}
