package aeminium.runtime.tools.benchmark;

public interface Reporter {
	public void startBenchmark(String name);
	public void reportLn(String line);
	public void stopBenchmark(String name);
	public void flush();
}
