package aeminiumruntime.tests;

import org.junit.Test;

import aeminiumruntime.Hint;


public class HintCreation extends BaseTest {
	@Test
	public void createLoopsHint() {
		Hint loops = Hint.createLoopsHint();
		assert( loops instanceof Hint.Loops);
	}

	@Test
	public void createRecursionHint() {
		Hint recursion = Hint.createRecursionHint();
		assert( recursion instanceof Hint.Recursion);
	}

	@Test
	public void createStepsHint() {
		Hint steps = Hint.createStepsHint(0xdeadbeef);
		assert( steps instanceof Hint.Steps);
		assert( ((Hint.Steps)steps).getStepCount() == 0xdeadbeef );
	}

}
