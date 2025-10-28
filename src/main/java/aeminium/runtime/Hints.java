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
	protected static final short OFFSET		= 8;
	/**
	 * Task has no hints.
	 */
	public static final short NO_HINTS		= 0;
	/**
	 * Task has loops.
	 */
	public static final short LOOPS			= 1<<(OFFSET+1);
	/**
	 * Task has recursions.
	 */
	public static final short RECURSION		= 1<<(OFFSET+2);
	/**
	 * Task cannot have dependents.
	 */
	public static final short NO_DEPENDENTS	= 1<<(OFFSET+3);
	/**
	 * Task cannot have children.
	 */
	public static final short NO_CHILDREN  	= 1<<(OFFSET+4);
	/**
	 * Task that are lightweight.
	 */
	public static final short SMALL  		= 1<<(OFFSET+5);
	/**
	 * Task that are lightweight.
	 */
	public static final short LARGE  		= 1<<(OFFSET+6);

	/**
	 * Convert hints to string representation.
	 *
	 * @param hints
	 * @return
	 */

	public static boolean check(short hints, short test) {
		return ((hints & test) == test);
	}

	private static void addIfHint(short hints, short test, String name, StringBuilder sb) {
		if ((hints & test) == test) sb.append(name + "|");
	}

	public static final String toString(short hints) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		addIfHint(hints, LOOPS, "LOOPS", sb);
		addIfHint(hints, RECURSION, "RECURSION", sb);
		addIfHint(hints, NO_DEPENDENTS, "NO_DEPENDENTS", sb);
		addIfHint(hints, NO_CHILDREN, "NO_CHILDREN", sb);
		addIfHint(hints, SMALL, "SMALL", sb);
		addIfHint(hints, LARGE, "LARGE", sb);
		if ( sb.length() > 1 ) sb.deleteCharAt(sb.length()-1);
		sb.append("]");
		return sb.toString();
	}
}
