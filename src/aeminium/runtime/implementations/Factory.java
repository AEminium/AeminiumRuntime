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
			rt = new ImplicitWorkStealingRuntime();
		}
		return rt;
	}	
}
