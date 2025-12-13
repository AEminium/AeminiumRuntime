package aeminium.runtime.helpers.loops;

import java.util.Iterator;

public class DoubleRange implements Iterable<Double> {

	public double start = 0;
	public double end = 0;
	public double increment = 0;

	public DoubleRange(double darts) {
		this(0, darts, 1);
	}

	public DoubleRange(double min, double max) {
		this(min, max, 1);
	}

	public DoubleRange(double min, double max, double inc) {
		this.start = min;
		this.end = max;
		this.increment = inc;
	}

	@Override
	public Iterator<Double> iterator() {
		return new Iterator<Double>() {

			private double c = start;

			@Override
			public boolean hasNext() {
				return c < (end-increment);
			}

			@Override
			public Double next() {
				c += increment;
				return c;
			}

			@Override
			public void remove() {
				// Ignore
			}

		};
	}


	public double getStart() {
		return start;
	}

	public double getEnd() {
		return end;
	}

	public double getIncrement() {
		return increment;
	}


}
