package aeminium.runtime.implementations.implicitworkstealing.decider;

import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.Factory;

public class DeciderFactory {
	protected static ParallelizationDecider decider;

	/**
	 * Prohibit Factory instantiation.
	 */
	protected DeciderFactory() {}

	/**
	 * Returns a new 'default' runtime object.
	 * @return
	 */
	public final static ParallelizationDecider getDecider() {
		String deciderConf = Configuration.getProperty(DeciderFactory.class, "implementation", "default");

		if ( decider == null ) {
			synchronized (DeciderFactory.class) {
				if ( deciderConf == null || deciderConf.equals("default")) {
					decider = new DefaultDecider();
				} else {
					// try to load runtime from specified class
					ClassLoader cl = Factory.class.getClassLoader();
					try {
						Class<?> klazz = cl.loadClass(deciderConf);
						Object obj = klazz.newInstance();
						decider = (ParallelizationDecider)obj;
					} catch (ClassNotFoundException e) {
						throw new Error("Cannot load runtime class : " + deciderConf,e);
					} catch (InstantiationException e) {
						throw new Error("Cannot instantiate class : " + deciderConf,e);
					} catch (IllegalAccessException e) {
						throw new Error("Cannot access class : " + deciderConf,e);
					}
				}
			}
		}
		return decider;
	}
}
