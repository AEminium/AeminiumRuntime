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
	protected static final long OFFSET     = 10;
	/**
	 * Task has no dependencies.
	 */
	public static final long NO_HINTS       = 0;
	/**
	 * Task has loops.
	 */
	public static final long LOOPS          = 1<<(OFFSET+1);
	/**
	 * Task has recursions.
	 */
	public static final long RECURSION      = 1<<(OFFSET+2);

	/**
	 * Convert hints to string representation.
	 *  
	 * @param hints
	 * @return
	 */
	public static String toString(long hints) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if ( (hints & LOOPS) == LOOPS )         sb.append("LOOPS|");
		if ( (hints & RECURSION) == RECURSION ) sb.append("RECURSION|");
		if ( sb.length() > 1 ) sb.deleteCharAt(sb.length()-1);
		sb.append("]");
		return sb.toString();
	}
}