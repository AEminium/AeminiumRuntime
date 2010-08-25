package aeminium.runtime.tools.benchmark.forkjoin;

public class BenchmarkExecutor {

	Benchmark[] tests;

	public BenchmarkExecutor(Benchmark[] benchs) {
		tests = benchs;
	}

	public void run(String[] args) {
		if (args.length == 0 || args[0].equals("-l") || args[0].equals("--list")) {
			for (Benchmark test : tests) {
				System.out.println(test.getName());
			}
		} else if (args[0].equals("-c") || args[0].equals("--count")) {
			System.out.println(tests.length);
		} else {
			try {
				int i = Integer.parseInt(args[0]);
				run(i);
			} catch (NumberFormatException e) {
				StringBuilder builder = new StringBuilder(args[0]);
				for (int i=1;i<args.length;i++) {
					builder.append(" ").append(args[i]);
				}
				run(builder.toString());
			}
		}
	}

	private void run(Benchmark test) {
		long cold = test.run();
		long warm = test.run();
		System.out.println(String.format("%s: %d %d", test.getName(), cold,
				warm));
	}

	private void run(int i) {
		run(tests[i]);
	}

	private void run(String testName) {
		for (Benchmark test : tests) {
			if (test.getName().equals(testName)) {
				run(test);
			}
		}

	}
}
