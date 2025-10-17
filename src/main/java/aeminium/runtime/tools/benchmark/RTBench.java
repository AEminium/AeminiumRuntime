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

package aeminium.runtime.tools.benchmark;


public class RTBench {

	private static Benchmark[] benchmarks = {
		new TaskCreationBenchmark(),
		new IndependentTaskGraph(),
		new LinearTaskGraph(),
		new FixedParallelMaxDependencies(),
		new ChildTaskBenchmark(),
		new FibonacciBenchmark()
	};

	public static void usage() {
		System.out.println();
		System.out.println("java aeminium.runtime.tools.benchmark.RTBench COMMAND");
		System.out.println("");
		System.out.println("COMMANDS:");
		System.out.println(" list             - List available benchmarks.");
		System.out.println(" run BENCHMARK    - Run specified benchmark.");
		System.out.println();
	}

	public static void main(String[] args) {


		if ( args.length == 0 ) {
			usage();
			return;
		}

		if ( args[0].equals("list") ) {
			for( Benchmark benchmark : benchmarks ) {
				System.out.println(benchmark.getName());
			}
		} else if ( args[0].equals("run") && args.length == 2 ) {
			Benchmark benchmark = null;
			for ( Benchmark b : benchmarks ) {
				if ( b.getName().equals(args[1])) {
					benchmark = b;
				}
			}

			if ( benchmark != null ) {
				Reporter reporter = new StringBuilderReporter();
				reporter.startBenchmark(benchmark.getName());
				benchmark.run(reporter);
				reporter.stopBenchmark(benchmark.getName());
				reporter.flush();
			} else {
				usage();
			}

		} else {
			usage();
		}
	}

	protected static void reportVMStats(Reporter reporter) {
		reporter.reportLn(String.format("Memory (TOTAL/MAX/FREE) (%d,%d,%d)", Runtime.getRuntime().totalMemory(),
																 			  Runtime.getRuntime().maxMemory(),
																 			  Runtime.getRuntime().freeMemory()));
	}
}
