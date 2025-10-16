package aeminium.runtime.helpers.loops;

import java.util.Iterator;

public class LongRange implements Iterable<Long> {

	public long start = 0;
	public long end = 0;
	public long increment = 0;

	public LongRange(long darts) {
		this(0, darts, 1);
	}

	public LongRange(long min, long max) {
		this(min, max, 1);
	}

	public LongRange(long min, long max, long inc) {
		this.start = min;
		this.end = max;
		this.increment = inc;
	}

	@Override
	public Iterator<Long> iterator() {
		return new Iterator<Long>() {

			private long c = start;

			@Override
			public boolean hasNext() {
				return c < (end-increment);
			}

			@Override
			public Long next() {
				c += increment;
				return c;
			}

			@Override
			public void remove() {
				// Ignore
			}

		};
	}


	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public long getIncrement() {
		return increment;
	}


}
