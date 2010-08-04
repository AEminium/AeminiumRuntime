package aeminium.runtime;

/* interface for base hint */
public class Hints {
	private static Hints.Loops loopsHint = new Loops();
	private static Hints.Recursion recursionHint = new Recursion();
	
	/* private protected constructor to prevent instantiation */
	protected Hints() {}
	
	public static Hints createLoopsHint() {
		return loopsHint;
	}
	
	public static Hints createRecursionHint() {
		return recursionHint;
	}
	
	public static Hints createStepsHint(long count) {
		return new Steps(count);
	}
	
	/* task contains loops */
	public static class Loops extends Hints {
		/* private protected constructor to prevent instantiation from outside */
		protected Loops() {}
	};

	/* task contains recursion */
	public static class Recursion extends Hints  {
		/* private protected constructor to prevent instantiation from outside */
		protected Recursion() {}
	};

	/* estimated virtual execution steps */
	public static class Steps extends Hints {
		private long stepCount = 0;
	
		/* private protected constructor to prevent instantiation from outside */
		protected Steps(long count){
			this.stepCount = count;
		}
		public long getStepCount() {
			return stepCount;
		}
	};
}