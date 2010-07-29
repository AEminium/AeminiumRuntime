package aeminium.runtime.tools.benchmark.forkjoin;

import aeminium.runtime.tools.benchmark.StringBuilderReporter;

public class SimpleStringBuilderReporter extends StringBuilderReporter {
	@Override
	public void startBenchmark(String name) {
		reportLn(String.format("TEST: %s", name));
	}

	@Override
	public void stopBenchmark(String name) {
	}
}
