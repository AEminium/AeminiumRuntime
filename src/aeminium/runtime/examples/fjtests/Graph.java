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

import java.util.Random;

public class Graph {
        public int value;
        public Graph[] children;

		public Graph(int value, int n) {
                this.value = value;
                this.children = new Graph[n];
        }
        
        public void add(int index, Graph node) {
                children[index] = node;
        }
        
        public static Graph randomIntGraph(int depth, int width, Random r) {
                Graph root = new Graph(1 /*r.nextInt() */, (depth>0) ? width : 0);
                if (depth > 0) {
                        for (int i=0; i < width; i++) {
                                root.add(i, randomIntGraph(depth-1, width, r));
                        }
                }
                return root;
        }
        
}
