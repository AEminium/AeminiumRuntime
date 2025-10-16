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

package aeminium.runtime.examples;

import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;

public class AtomicTest {

	private static int MAX_ITEMS = 100;

    public static void main(String[] args) {
        final Runtime rt = Factory.getRuntime();
        rt.init();


        final DataGroup d1 = rt.createDataGroup();

        for (int i = 0; i < MAX_ITEMS; i++) {
        	final int ii = i;
        	Task ti = rt.createAtomicTask(new Body() {
				@Override
				public void execute(Runtime rt, Task current) {
					System.out.println("i:" + ii);
					for (int j=0; j < MAX_ITEMS/2; j++) {
						System.out.print(j + " ");
					}
					System.out.println(".");
				}
        	}, d1, Runtime.NO_HINTS);
        	rt.schedule(ti, Runtime.NO_PARENT, Runtime.NO_DEPS);
        }

        rt.shutdown();
    }
}
