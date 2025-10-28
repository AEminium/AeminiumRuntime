package aeminium.runtime.examples.futures.montecarlo;

import java.util.Random;
import java.util.stream.LongStream;

import aeminium.runtime.futures.RuntimeManager;
import aeminium.runtime.futures.codegen.MapHelper;

public class FuturePi {
	public static void main(String[] args) {
		RuntimeManager.init();
		long dartsc = 100000;

		Random random = new Random(1L);

		Integer score = MapHelper.map( LongStream.range(0, dartsc).spliterator(), (i) -> {
			double x_coord, y_coord, r;
			r = random.nextDouble();
			x_coord = (2.0 * r) - 1.0;
			r = random.nextDouble();
			y_coord = (2.0 * r) - 1.0;

			/* if dart lands in circle, increment score */
			if ((x_coord * x_coord + y_coord * y_coord) <= 1.0) return 1;
			else return 0;
		}).reduce((a,b) -> a+ b).get();

		double d = 4.0 * (double) score / (double) dartsc;
		System.out.println("PI = " + d);
		RuntimeManager.shutdown();
	}
}
