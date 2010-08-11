package aeminium.runtime.tools.benchmark;


public interface Benchmark {
	public void run(Reporter reporter);
	public String getName();
}
