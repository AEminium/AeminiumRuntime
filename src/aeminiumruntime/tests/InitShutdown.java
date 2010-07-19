package aeminiumruntime.tests;

import org.junit.Test;

import aeminiumruntime.Runtime;

public class InitShutdown extends BaseTest {
	@Test
	public void InitShutdownSingle() {
		Runtime rt = getRuntime();
		rt.init();
		rt.shutdown();
	}
	
	@Test 
	public void InitShutdownMultiple() {
		for (int i = 0; i < 100; i++ ) {
			InitShutdownSingle();
		}
	}
}
