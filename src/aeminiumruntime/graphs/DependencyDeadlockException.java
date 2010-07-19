package aeminiumruntime.graphs;

public class DependencyDeadlockException extends Exception {

	public DependencyDeadlockException(int id) {
		super("Deadlock in dependencies, related to task " + id);
	}

	private static final long serialVersionUID = -8055065175950572136L;

}
