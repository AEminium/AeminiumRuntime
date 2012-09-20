package aeminium.runtime.profiler.profilerTests.GeneticKnapsack;
import java.util.Comparator;

public class ComparatorOnFitness implements Comparator<Integer> {

	double[] popFit;
	public ComparatorOnFitness(double[] ft) {
		popFit = ft;
	}
	
	public int compare(Integer a, Integer b) {
		if (popFit[b] == popFit[a]) return 0;
		if (popFit[b] - popFit[a] > 0 ) return 1;
		return -1;
	}
}
