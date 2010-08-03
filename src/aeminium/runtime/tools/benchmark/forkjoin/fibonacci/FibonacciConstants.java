package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

public interface FibonacciConstants {
	// This value is the one used in Doug Lea's Paper on ForkJoin
	public static final int MAX_CALC = 46;
	// threshold when to switch over to seqential version
	public static final int THRESHOLD = 13;
}
