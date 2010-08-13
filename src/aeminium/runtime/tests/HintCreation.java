package aeminium.runtime.tests;

import org.junit.Test;
import static org.junit.Assert.*;

import aeminium.runtime.Hints;


public class HintCreation extends BaseTest {
	
	@Test
	public void hintsString() {
		long hints = Hints.LOOPS + Hints.RECURSION;
		assertTrue( Hints.toString(hints).equals("[LOOPS|RECURSION]") );
	}

}
