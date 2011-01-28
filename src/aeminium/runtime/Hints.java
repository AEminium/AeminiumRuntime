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