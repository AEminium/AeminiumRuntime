package aeminium.runtime.tests;

import org.junit.Test;

import aeminium.runtime.Runtime;

public class InitShutdown extends BaseTest {
	@Test
	public void initShutdownSingle() {
		Runtime rt = getRuntime();
		rt.init();
		rt.shutdown();
	}
	
	@Test 
	public void initShutdownMultiple() {
		for (int i = 0; i < 100; i++ ) {
			initShutdownSingle();
		}
	}
}
