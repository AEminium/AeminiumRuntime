package aeminium.runtime.examples.futures.montecarlo;

import java.util.Random;

public class SeqPi {
	public static void main(String[] args) {
		long dartsc = 100000;

		Random random = new Random(1L);
		double x_coord, y_coord, r;
		long score = 0;
		for (long n = 1; n <= dartsc; n++) {
			/* generate random numbers for x and y coordinates */
			r = random.nextDouble();
			x_coord = (2.0 * r) - 1.0;
			r = random.nextDouble();
			y_coord = (2.0 * r) - 1.0;

			/* if dart lands in circle, increment score */
			if ((x_coord * x_coord + y_coord * y_coord) <= 1.0) score++;
		}
		double d = 4.0 * (double) score / (double) dartsc;
		System.out.println("PI = " + d);
	}
}
