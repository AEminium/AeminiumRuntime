package aeminiumruntime.prioritizers;

public interface PrioritizableTaskGraph {
	public <T> int countDependencies(T task);
}
