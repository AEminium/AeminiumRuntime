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

package aeminium.runtime.examples.fjtests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import aeminium.runtime.*;
import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Factory;

public class AeminiumLCS {

	private int blockSize;
	private int M;
	private int N;
	private int [][] matrix;
	private String x;
	private String y;
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private int[][] taskIndex;
	public String solution;
	
	public AeminiumLCS(int blockS) {
		this.blockSize = blockS;
	}
	
	
	private Collection<Task> getDeps(int ii, int jj) {
		if (ii == 0 && jj == 0) return Runtime.NO_DEPS;
		ArrayList<Task> deps = new ArrayList<Task>();
		for(int i=0; i <= ii; i++) {
			for(int j=0; j <= jj; j++) {
				if (j != jj || i != ii) {
					deps.add(getTask(i, j));
				}
			}	
		}
		return deps;
	}
	
	private Task getTask(int ii, int jj) {
		return tasks.get(taskIndex[ii][jj]);
	}
	
	public void compute(Runtime rt, String a, String b) {
		x = a;
		y = b;
		M = x.length();
		N = y.length();
		matrix = new int[M+1][N+1];
		int tx = (int)Math.floor(M/blockSize) + 1;
		int ty = (int)Math.floor(N/blockSize) + 1;
		taskIndex = new int[tx][ty];
		int ii = 0;
		int jj = 0;
		int fi = 0;
		int fj = 0;
		
		for(int im = M-1; im >= 0; im -= blockSize, ii++) {
			jj = 0;
			for(int jm = N-1; jm >= 0; jm -= blockSize, jj++) {
				
				Task t = rt.createNonBlockingTask(this.createBody(im, jm), Runtime.NO_HINTS);
				tasks.add(t);
				taskIndex[ii][jj] = tasks.size()-1;
				fi = ii;
				fj = jj;
				rt.schedule(
						getTask(ii, jj), Runtime.NO_PARENT, 
						this.getDeps(ii,jj));
			}
			while (jj < ty) {
				tasks.add(null);
				jj++;
			}
		}
		while (ii < ty) {
			tasks.add(null);
			ii++;
		}
		Body mergeBody = new Body() {

			@Override
			public void execute(Runtime rt, Task current) {
				StringBuilder sol = new StringBuilder();
				int i = 0, j = 0;
		        while(i < M && j < N) {
		            if (x.charAt(i) == y.charAt(j)) {
		            	sol.append(x.charAt(i));
		                i++;
		                j++;
		            }
		            else if (matrix[i+1][j] >= matrix[i][j+1]) i++;
		            else                                 j++;
		        }
		        solution = sol.toString();
			}
			
		};
		Task merge = rt.createNonBlockingTask(mergeBody, Runtime.NO_HINTS);
		rt.schedule(merge, Runtime.NO_PARENT, Arrays.asList(
				getTask(fi,fj)));
	}


	private Body createBody(final int I, final int J) {
		return new Body() {

			@Override
			public void execute(Runtime rt, Task current) {
				for (int i = I; i >= 0 && i > (I - blockSize); i--) {
					for (int j = J; i >= 0 && j > (J - blockSize); j--) {
						if (x.charAt(i) == y.charAt(j))
		                    matrix[i][j] = matrix[i+1][j+1] + 1;
		                else 
		                    matrix[i][j] = Math.max(matrix[i+1][j], matrix[i][j+1]);
					}
				}
			}
		};
	}
	
	
	public static void main(String[] args) {		
		
		String s1 = "The quick fox jumps over the lazy dog.";
		String s2 = "Jacob is a very lazy dog.";
		
		AeminiumLCS gen = new AeminiumLCS(5);
		
		Runtime rt = Factory.getRuntime();
		rt.init();
		gen.compute(rt, s1, s2);
		rt.shutdown();
		
		System.out.println(gen.solution);
		
	}

	
}
