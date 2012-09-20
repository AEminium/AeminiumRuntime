package aeminium.runtime.profiler.profilerTests.GeneticKnapsack;
public class Indiv implements Comparable<Indiv> {
	public boolean[] has;
	
	public double fitness = 0;
	public int size = 0;
	
	public Indiv(int size) {
		has = new boolean[size];
	}
	
	public void add(int w, boolean h) {
		has[size] = h;
		size++;
	}
	
	public int compareTo(Indiv other) {
	    if (this.fitness == other.fitness) {
	      return 0;
	    } else if (this.fitness > other.fitness) {
	      return 1;
	    } else {
	      return -1;
	    }
	  }
}
