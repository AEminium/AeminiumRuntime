package aeminium.runtime.implementations;

import java.util.EnumSet;
import java.util.Map;

import aeminium.runtime.implementations.Factory.RuntimeConfiguration;

public final class Configuration {
	protected static final String RT_PREFIX = "AEMINIUM_RT_";
	protected static final int processorCount;
	protected static final String implementation;
	protected static final EnumSet<Flags> flags;
	
	static {
		// processor count
		String processorEnv = System.getenv(RT_PREFIX+"PROCESSORS");
		if ( processorEnv != null && Integer.valueOf(processorEnv) > 0 ) {
			processorCount = Integer.valueOf(processorEnv);
		} else {
			processorCount = Runtime.getRuntime().availableProcessors();
		}
			
		// implementation
		String implEnv = System.getenv(RT_PREFIX+"IMPLEMENTATION");
		if ( implEnv != null ) {
			implementation = implEnv;
		} else {
			implementation = "default";
		}
		
		// parse flags from environment
		flags = EnumSet.noneOf(Flags.class);
		for ( Flags f : Flags.values() ) {
			Map<String, String> env = System.getenv();
			if ( env.containsKey(RT_PREFIX + f.name())) {
				flags.add(f);
			}
		}
		
	}
	
	protected Configuration() {}
	
	public static int getProcessorCount() {
		return processorCount;
	}
	
	public static String getImplementation() {
		return implementation;
	}
	
	/**
	 * Computes flag set based on set environment variables.
	 * 
	 * @return
	 */
	public static EnumSet<Flags> getFlags() {
		return flags;
	}

	public static class ListImplementations {
		public static void main(String[] args) {
			for ( RuntimeConfiguration<?> rc : Factory.getImplementations().values()  ) {
				System.out.println(rc.getName());
			}
		}
	}

}
