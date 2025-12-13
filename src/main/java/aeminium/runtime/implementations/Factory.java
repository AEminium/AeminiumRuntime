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

package aeminium.runtime.implementations;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.implicitworkstealing.ImplicitWorkStealingRuntime;

public class Factory {
	protected static Runtime rt;

	/**
	 * Prohibit Factory instantiation.
	 */
	protected Factory() {}

	/**
	 * Returns a new 'default' runtime object.
	 * @return
	 */
	public final static Runtime getRuntime() {
		//return getRuntime(Configuration.getImplementation());
		if ( rt == null ) {
			synchronized (Factory.class) {
				if ( Configuration.getImplementation().equals("default")) {
					rt = new ImplicitWorkStealingRuntime();
				} else {
					// try to load runtime from specified class
					ClassLoader cl = Factory.class.getClassLoader();
					try {
						Class<?> klazz = cl.loadClass(Configuration.getImplementation());
						Object obj = klazz.newInstance();
						rt = (Runtime)obj;
					} catch (ClassNotFoundException e) {
						throw new Error("Cannot load runtime class : " + Configuration.getImplementation(),e);
					} catch (InstantiationException e) {
						throw new Error("Cannot instantiate class : " + Configuration.getImplementation(),e);
					} catch (IllegalAccessException e) {
						throw new Error("Cannot access class : " + Configuration.getImplementation(),e);
					}
				}
			}
		}
		return rt;
	}
}
