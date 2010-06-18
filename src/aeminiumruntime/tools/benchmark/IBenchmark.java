package aeminiumruntime.tools.benchmark;

public interface IBenchmark {
	public void run(IReporter reporter);
	public String getName();
	
}
