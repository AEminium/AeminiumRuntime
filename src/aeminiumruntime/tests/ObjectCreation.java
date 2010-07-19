package aeminiumruntime.tests;

import org.junit.Test;

import aeminiumruntime.DataGroup;
import aeminiumruntime.Runtime;

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
