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
