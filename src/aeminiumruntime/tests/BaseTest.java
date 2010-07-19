package aeminiumruntime.tests;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;

import aeminiumruntime.Runtime;
import aeminiumruntime.launcher.RuntimeFactory;

public abstract class BaseTest {
	private Runtime rt;
	private Logger log;
	
	public BaseTest() {
		log = Logger.getLogger(this.getClass().getName());
		log.setLevel(Level.FINE);
	}
	
	@Before
	public void setUp() throws Exception {
		rt = RuntimeFactory.getRuntime();
	}

	protected Runtime getRuntime() {
		return rt;
	}

	protected Logger getLogger() {
		return log;
	}
}
