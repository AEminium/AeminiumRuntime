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

package aeminium.runtime.tests;

import org.junit.Test;

import aeminium.runtime.DataGroup;
import aeminium.runtime.Runtime;

public class ObjectCreation extends BaseTest {
	@Test
	public void createDataGroup() {
		Runtime rt  = getRuntime();
		rt.init();
		
		@SuppressWarnings("unused")
		DataGroup dg = rt.createDataGroup();
		// to calm findbugs
		dg = null;
		
		rt.shutdown();
	}
}
