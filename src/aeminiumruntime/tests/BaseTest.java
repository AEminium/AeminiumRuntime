package aeminiumruntime.tests;


import org.junit.Before;
import aeminiumruntime.Runtime;
import aeminiumruntime.queue.QRuntime;

public abstract class BaseTest {
	private Runtime rt;
	
	@Before
	public void setUp() throws Exception {
		rt = new QRuntime();
	}

	protected Runtime getRuntime() {
		return rt;
	}
}
