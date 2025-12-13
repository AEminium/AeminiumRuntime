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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;



/*
 * Loads configuration info from aeminiumrt.config file.
 * The AEMINIUMRT_CONFIG env variable can be used to override the file.
 */
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
			FileReader freader;
			try {
				freader = new FileReader(file);
				properties.load(freader);
				freader.close();
			}  catch (IOException e) {
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
		String value = properties.getProperty(klazz.getSimpleName() + "." + key);
		if ( value != null ) {
			return value.trim();
		} else {
			return defaultValue;
		}
	}

	public final static int getProperty(Class<?> klazz, String key, int defaultValue) {
		String value = properties.getProperty(klazz.getSimpleName()+ "." + key);
		if ( value != null ) {
			return Integer.valueOf(value.trim());
		} else {
			return defaultValue;
		}
	}

	public final static boolean getProperty(Class<?> klazz, String key, boolean defaultValue) {
		String value = properties.getProperty(klazz.getSimpleName()+ "." + key);
		if ( value != null ) {
			return Boolean.valueOf(value.trim());
		} else {
			return defaultValue;
		}
	}

}
