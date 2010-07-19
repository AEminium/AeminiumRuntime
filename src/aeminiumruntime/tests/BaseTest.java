package aeminiumruntime.tests;

import org.junit.Before;
import aeminiumruntime.Runtime;
import aeminiumruntime.launcher.RuntimeFactory;

public abstract class BaseTest {
	private Runtime rt;
	
	@Before
	public void setUp() throws Exception {
		rt = RuntimeFactory.getRuntime();
	}

	protected Runtime getRuntime() {
		return rt;
	}
}
