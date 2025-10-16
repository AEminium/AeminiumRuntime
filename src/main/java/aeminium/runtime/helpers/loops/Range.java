package aeminium.runtime.helpers.loops;

import java.util.Iterator;

public class Range implements Iterable<Integer> {

	public int start = 0;
	public int end = 0;
	public int increment = 0;

	public Range(int darts) {
		this(0, darts, 1);
	}

	public Range(int min, int max) {
		this(min, max, 1);
	}

	public Range(int min, int max, int inc) {
		this.start = min;
		this.end = max;
		this.increment = inc;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			private int c = start;

			@Override
			public boolean hasNext() {
				return c < (end-increment);
			}

			@Override
			public Integer next() {
				c += increment;
				return c;
			}

			@Override
			public void remove() {
				// Ignore
			}

		};
	}


	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getIncrement() {
		return increment;
	}


}
