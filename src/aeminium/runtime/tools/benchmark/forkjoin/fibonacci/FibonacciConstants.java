package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

public interface FibonacciConstants {
	// This value is the one used in Doug Lea's Paper on ForkJoin
	public static final int MAX_CALC = 46;
	// threshold when to switch over to sequential version
	public static final int THRESHOLD = 13;
	// format string for results: "#CPUS COLD WARM"
	public static final String RESULT_FORMAT = "%12d %12d %12d";
}
