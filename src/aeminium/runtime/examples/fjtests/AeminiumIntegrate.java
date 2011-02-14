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

package aeminium.runtime.examples.fjtests;

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/*
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Bases on the code written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

import java.util.Arrays;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class AeminiumIntegrate {

	static final double errorTolerance = 1.0e-11;
	static double threshold = 10;

	static double computeFunction(double x) {
		return (x * x + 1.0) * x;
	}

	static final double start = -2101.0;
	static final double end = 200.0;
	/*
	 * The number of recursive calls for integrate from start to end.
	 * (Empirically determined)
	 */
	static final int calls = 263479047;

	public static Task recursiveCall(final Runtime rt, final Task parent,
			final double l, final double r, final double a) {
		Task t = rt.createNonBlockingTask(new Body() {

			private double recEval(double l, double r, double fl, double fr,
					double a) {
				double h = (r - l) * 0.5;
				double c = l + h;
				double fc = (c * c + 1.0) * c;
				double hh = h * 0.5;
				double al = (fl + fc) * hh;
				double ar = (fr + fc) * hh;
				double alr = al + ar;
				if (Math.abs(alr - a) <= errorTolerance)
					return alr;
				else
					return recEval(c, r, fc, fr, ar)
							+ recEval(l, c, fl, fc, al);
			}

			@Override
			public void execute(Runtime rt, final Task current) {
				double fl = (l * l + 1.0) * l;
				double fr = (r * r + 1.0) * r;
				double h = (r - l) * 0.5;
				double c = l + h;
				double fc = (c * c + 1.0) * c;
				double hh = h * 0.5;
				double al = (fl + fc) * hh;
				double ar = (fr + fc) * hh;
				double alr = al + ar;

				// Base case
				if (Math.abs(alr - a) <= errorTolerance) {
					current.setResult(alr);
					return;
				}

				if (Math.abs(alr - a) <= threshold) {
					// Threshold for task
					current.setResult(recEval(l, r, (l * l + 1.0) * l, (r * r + 1.0) * r, a));
					return;
				}

				final Task branch1 = recursiveCall(rt, current, l, c, al);
				final Task branch2 = recursiveCall(rt, current, c, r, ar);

				Task merge = rt.createNonBlockingTask(new Body() {
					public void execute(Runtime rt, Task p) {
						double r1 = (Double) branch1.getResult();
						double r2 = (Double) branch2.getResult();
						current.setResult(r1 + r2);
					}

				}, Runtime.NO_HINTS);
				rt.schedule(merge, current, Arrays.asList(branch1, branch2));

			}
		}, Runtime.NO_HINTS);

		rt.schedule(t, parent, Runtime.NO_DEPS);
		return t;
	}

	public static void main(String[] args) {
		final Runtime rt = Factory.getRuntime();
		rt.init();

		Task t1 = rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task p) {
				final Task calc = recursiveCall(rt, p, start, end, 0);
				;

				Task print = rt.createBlockingTask(new Body() {
					@Override
					public void execute(Runtime rt, Task p) {
						System.out.println("Final result:" + calc.getResult());
					}
				}, Runtime.NO_HINTS);
				rt.schedule(print, p, Arrays.asList(calc));
			}

			@Override
			public String toString() {
				return "Master Task";
			}

		}, Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.shutdown();
	}
}