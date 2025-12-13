package aeminium.runtime.futures.codegen;

import aeminium.runtime.Task;
import aeminium.runtime.futures.FutureChild;

public class WhileHelper {

	// Recursive Implementation of While in Tasks
	public static void whileLoop(final Task currentTask, final Expression<Boolean> e,
			final Expression<Void> body) {
		if (e.evaluate(currentTask)) {

			final FutureChild<Void> b = new FutureChild<Void>((t) -> { body.evaluate(currentTask); return null; }, currentTask);

			new FutureChild<Void>((t) -> { WhileHelper.whileLoop(t, e, body); return null; }, currentTask, b);
		}

	}
}
