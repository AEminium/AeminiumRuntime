package aeminiumruntime.tools.benchmark;

public class StringBuilderReporter implements IReporter {
	final private StringBuilder sb = new StringBuilder();
	final private String EOL = System.getProperty("line.separator");
	
	@Override
	public void reportLn(String line) {
		sb.append(line + EOL);
	}

	@Override
	public void startBenchmark(String name) {
		reportLn(String.format("==[ BEGIN : %15s ]==================================", name));
	}

	@Override
	public void stopBenchmark(String name) {
		reportLn(String.format("==[ END   : %15s ]==================================", name));
	}

	@Override
	public void flush() {
		System.out.print(sb.toString());
		sb.setLength(0);
	}
}
