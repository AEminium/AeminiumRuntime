package aeminiumruntime.tools.benchmark;

public interface IReporter {
	public void startBenchmark(String name);
	public void reportLn(String line);
	public void stopBenchmark(String name);
	public void flush();
}
