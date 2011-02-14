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

package aeminium.runtime;

/**
 * Task hints.
 * 
 * @author sven
 *
 */
public final class Hints {
	private Hints() {}
	
	/**
	 * Base offset to leave the lower bits unused.
	 */
	protected static final short OFFSET     = 8;
	/**
	 * Task has no dependencies.
	 */
	public static final short NO_HINTS       = 0;
	/**
	 * Task has loops.
	 */
	public static final short LOOPS          = 1<<(OFFSET+1);
	/**
	 * Task has recursions.
	 */
	public static final short RECURSION      = 1<<(OFFSET+2);

	/**
	 * Convert hints to string representation.
	 *  
	 * @param hints
	 * @return
	 */
	public static final String toString(short hints) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if ( (hints & LOOPS) == LOOPS )         sb.append("LOOPS|");
		if ( (hints & RECURSION) == RECURSION ) sb.append("RECURSION|");
		if ( sb.length() > 1 ) sb.deleteCharAt(sb.length()-1);
		sb.append("]");
		return sb.toString();
	}
}