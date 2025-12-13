package aeminium.runtime.tools.benchmark;

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

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class AeminiumFibonacci {

	public static class FibBody implements Body {
		public volatile int value;
		private int threshold;

		public FibBody(int n, int threshold) {
			this.value = n;
			this.threshold = threshold;
		}

		public int seqFib(int n) {
			if (n <= 2) return 1;
			else return (seqFib(n - 1) + seqFib(n - 2));
		}

		@Override
		public void execute(Runtime rt, Task current) {

			if ( value <= threshold  ) {
				value = seqFib(value);
			} else {
				FibBody b1 = new FibBody(value - 1, threshold);
				Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
				rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);

				FibBody b2 = new FibBody(value - 2, threshold);
				Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
				rt.schedule(t2, Runtime.NO_PARENT, Runtime.NO_DEPS);

				t1.getResult();
				t2.getResult();
				value = b1.value + b2.value;
			}

			for (long i = 0; i < 1000000000l; i++)
			{
				i--;
				i++;
			}
		}
	}

	public static Body createFibBody(final Runtime rt, final int n, int threshold) {
		return new  AeminiumFibonacci.FibBody(n, threshold);
	}

	public static void main(String[] args) {
		Runtime rt = Factory.getRuntime();
		rt.init();
		FibBody body = new AeminiumFibonacci.FibBody(30, 16);
		Task t1 = rt.createNonBlockingTask(body, Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.shutdown();

		System.out.println("F(6) = " + body.value);
	}
}
