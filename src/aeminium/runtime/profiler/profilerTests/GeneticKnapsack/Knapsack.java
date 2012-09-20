package aeminium.runtime.profiler.profilerTests.GeneticKnapsack;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.StringTokenizer;

public class Knapsack {
	public static int SIZE_LIMIT = 87;

	public final static Random rand = new Random();
	
	public final static int numberOfItems = 500;
	public final static Item[] items = Knapsack.readItems("items.txt", numberOfItems);
	
	int runs = 5;
	int popSize = 100;
	public final static int cromSize = numberOfItems;
	
	double prob_mut = 0.2;
	double prob_rec = 0.2;
	
	int numGen = 100000;

	
	
	public void run() {
		System.out.println("Running");
		for (int i=0; i<runs; i++) {
			ga();
		}
		System.out.println("Done");
	}
	
	/* Genetic Algorithm */
	public void ga() {
		
		// Initialize Population
		Indiv[] pop = new Indiv[popSize];
		Evaluator evaluator = new Evaluator();
		
		evaluator.execute(popSize, pop, true);
		
		for (int i=0; i< numGen; i++) {
			// Sort by lowest Fitness
			evaluator.execute(popSize, pop, false);
			Arrays.sort(pop);
			
			// System.out.println("Best fit:" + pop[2].fitness);
			
			Indiv[] parents = tournament(pop);
			
			// Recombination
			int half = parents.length/2;
			Recombiner recombiner = new Recombiner();
			recombiner.execute(half, parents, prob_rec);
		
			// Mutation and Evaluation
			Mutator mutator = new Mutator();
			mutator.execute(parents.length, parents, prob_rec);
			
			pop = parents;	
		}
		
		
		// Best:
		evaluator.execute(popSize, pop, false);
		
		
		Arrays.sort(pop);
		Indiv indiv = pop[0];
		int value = 0;
		int weight = 0;
		for (int i=0; i< indiv.size; i++) {
			if (indiv.has[i]) {
				value += items[i].value;
				weight += items[i].weight;
			}
		}
		System.out.println("Best value/weight: " + value + ", " + weight);
		
	}

	private Indiv[] tournament(Indiv[] pop) {
		int tSize = 50;
		Indiv[] f = new Indiv[popSize];
		for (int i = 0; i < popSize; i++) {
			Collections.shuffle(Arrays.asList(pop));
			f[i] = pop[0];
			for (int j = 1; j < tSize; j++) {
				if (pop[j].fitness < f[i].fitness) f[i] = pop[j];
			}
		}
		return f;
	}

	public static void main(String[] args) {
		new Knapsack().run();
	}
	
	public static Item[] readItems(String fname, int n) {
		if (!new File(fname).exists())
			return null;
		
		Item[] tmp = new Item[n];
		try {
			FileInputStream fstream = new FileInputStream(fname);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i = 0;
			while ((strLine = br.readLine()) != null) {
				StringTokenizer tok = new StringTokenizer(strLine, ",");
				tmp[i++] = new Item(tok.nextToken(), Float.parseFloat(tok.nextToken()), Float.parseFloat(tok.nextToken()));
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return tmp;
	}

}
