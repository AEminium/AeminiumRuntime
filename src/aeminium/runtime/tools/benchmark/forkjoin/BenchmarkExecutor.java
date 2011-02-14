/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 * 
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		System.gc();
		try { Thread.sleep(1000); } catch (Exception e){}
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
