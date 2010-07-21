package aeminium.runtime.tests;

import org.junit.Test;

import aeminium.runtime.DataGroup;
import aeminium.runtime.Runtime;

public class ObjectCreation extends BaseTest {
	@Test
	public void createDataGroup() {
		Runtime rt  = getRuntime();
		rt.init();
		
		@SuppressWarnings("unused")
		DataGroup dg = rt.createDataGroup();
		
		rt.shutdown();
	}
}
