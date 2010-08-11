package aeminium.runtime.implementations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import aeminium.runtime.implementations.Factory.RuntimeConfiguration;

public final class Configuration {
	protected static final String GLOBAL_PREFIX = "global.";
	protected static int processorCount;
	protected static String implementation;
	protected static final Properties properties; 
	
	static {
		String filename = System.getenv("AEMINIUMRT_CONFIG");
		if ( filename == null ) {
			filename = "aeminiumrt.config";
		}
		File file = new File(filename);
		properties = new Properties();
		if ( file.exists()  && file.canRead()) {
			try {
				properties.load(new FileReader(file));
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		} 
		
		// processor count
		String processorCount = properties.getProperty(GLOBAL_PREFIX + "processorCount");
		if (processorCount != null ) {
			Configuration.processorCount = Integer.valueOf(processorCount);
		} else {
			Configuration.processorCount = Runtime.getRuntime().availableProcessors();
		}
			
		// implementation
		String implementation = properties.getProperty(GLOBAL_PREFIX + "implementation");
		if ( implementation != null ) {
			Configuration.implementation = implementation;
		} else {
			Configuration.implementation = "default";
		}
	}
	
	protected Configuration() {}
	
	public final static int getProcessorCount() {
		return processorCount;
	}
	
	public final static String getImplementation() {
		return implementation;
	}

	public final static String getProperty(Class<?> klazz, String key, String defaultValue) {
		String value = properties.getProperty(klazz.getName() + "." + key);
		if ( value != null ) {
			return value;
		} else {
			return defaultValue;
		}
	}
	
	public final static int getProperty(Class<?> klazz, String key, int defaultValue) {
		String value = properties.getProperty(klazz.getSimpleName()+ "." + key);
		if ( value != null ) {
			return Integer.valueOf(value);
		} else {
			return defaultValue;
		}
	}
	
	public final static boolean getProperty(Class<?> klazz, String key, boolean defaultValue) {
		String value = properties.getProperty(klazz.getSimpleName()+ "." + key);
		if ( value != null ) {
			return Boolean.valueOf(value);
		} else {
			return defaultValue;
		}
	}
	
	public final static class ListImplementations {
		public static void main(String[] args) {
			for ( RuntimeConfiguration<?> rc : Factory.getImplementations().values()  ) {
				System.out.println(rc.getName());
			}
		}
	}
}