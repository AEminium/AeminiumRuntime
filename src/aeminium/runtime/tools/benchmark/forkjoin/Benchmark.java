package aeminium.runtime.tools.benchmark.forkjoin;

public interface Benchmark {
	public String getName();
	public long run();
}
