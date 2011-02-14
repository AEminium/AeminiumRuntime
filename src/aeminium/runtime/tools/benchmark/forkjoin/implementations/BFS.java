/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 * 
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

package aeminium.runtime.tools.benchmark.forkjoin.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import aeminium.runtime.examples.fjtests.Graph;

import jsr166y.RecursiveAction;

public class BFS extends RecursiveAction {

	private static final long serialVersionUID = 1L;
	private int found;
	private int target;
	private Graph graph;
	private int threshold;

	public BFS(int target, Graph graph, int threshold) {
		this.target = target;
		this.graph = graph;
		this.threshold = threshold;
	}

	public int seqCount() {
		return seqCount(target, graph);
	}
	
	public static int seqCount(int target, Graph graph) {
		int t;
		if (target == graph.value)
			t = 1;
		else
			t = 0;

		for (int i = 0; i < graph.children.length; i++) {
			t += seqCount(target, graph.children[0]);
		}
		return t;
	}
	
	public int parCount() {
		compute();
		return found;
	}

	@Override
	protected void compute() {

		if (probe(graph, threshold)) {
			found = seqCount();
		} else {
			if (target == graph.value) found = 1; else found = 0;
			Collection<BFS> futures = new ArrayList<BFS>();
			BFS tmp;
			for (int i=0;i<graph.children.length;i++) {
				tmp = new BFS(target, graph.children[i], threshold);
				invokeAll(tmp);
				futures.add(tmp);
			}
			for (BFS finder : futures) {
				try {
					finder.get();
					found += finder.found;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	public static boolean probe(Graph graph, int threshold) {
		Graph tmp = graph;
		while(tmp.children.length > 0) {
			if (threshold-- < 0) return false;
			tmp = tmp.children[0];
		}
		return true;
		
	}

	public static void main(String[] args) {
		int target = 1;

		Random r = new Random(1234567890);
		Graph g = Graph.randomIntGraph(23, 2, r);
		System.out.println("Created Graph");
		BFS searcher = new BFS(target, g, 21);
		long start = System.nanoTime();
		int f = searcher.seqCount();
		long end = System.nanoTime();
		System.out.println("Found " + f + " occurrences of " + target
				+ " and took " + (end - start) + " nanoseconds.");
		
		
		start = System.nanoTime();
		f = searcher.parCount();
		end = System.nanoTime();
		System.out.println("Found " + f + " occurrences of " + target
				+ " and took " + (end - start) + " nanoseconds.");

	}

}
