package aeminiumruntime;

public class CyclicDependencyError extends RuntimeError {
	private static final long serialVersionUID = -6497603741014251936L;

	public CyclicDependencyError(String msg) {
		super(msg);
	}
}
