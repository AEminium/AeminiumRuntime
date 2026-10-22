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

package aeminium.runtime.examples;
public class Tests {

	public static int power(int a,int b){
		if(b==1)
			return a;
		else{
			System.out.println("Power: "+b);
			return a*power(a,b-1);
		}
	}

	public static void matrixMultiplication(){
		/*  a*b=c */
		int [][] a = {{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2},{2,2,2}};
		int [][] b = {{2,2,2,2,2},{2,2,2,2,2},{2,2,2,2,2}};
		int [][] c = new int [a.length][5];
		int i,j,k;

		for(i=0;i<a.length;i++){
			for(j=0;j<b[0].length;j++){
				for(k=0;k<a[i].length;k++){
					c[i][j]+=a[i][k]*b[k][j];
				}
			}
			System.out.println("Row "+i+" from matrix A calculated.");
		}
	}
}
