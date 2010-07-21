package aeminium.runtime.tests;

import org.junit.Test;

import aeminium.runtime.Hints;


public class HintCreation extends BaseTest {
	
	@Test
	public void createLoopsHint() {
		Hints loops = Hints.createLoopsHint();
		assert( loops instanceof Hints.Loops);
	}

	@Test
	public void createRecursionHint() {
		Hints recursion = Hints.createRecursionHint();
		assert( recursion instanceof Hints.Recursion);
	}

	@Test
	public void createStepsHint() {
		Hints steps = Hints.createStepsHint(0xdeadbeef);
		assert( steps instanceof Hints.Steps);
		assert( ((Hints.Steps)steps).getStepCount() == 0xdeadbeef );
	}

}
