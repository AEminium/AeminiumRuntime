package aeminium.runtime.helpers.loops;

import java.util.Iterator;

public class FloatRange implements Iterable<Float> {

	public float start = 0;
	public float end = 0;
	public float increment = 0;

	public FloatRange(float darts) {
		this(0, darts, 1);
	}

	public FloatRange(float min, float max) {
		this(min, max, 1);
	}

	public FloatRange(float min, float max, float inc) {
		this.start = min;
		this.end = max;
		this.increment = inc;
	}

	@Override
	public Iterator<Float> iterator() {
		return new Iterator<Float>() {

			private float c = start;

			@Override
			public boolean hasNext() {
				return c < (end-increment);
			}

			@Override
			public Float next() {
				c += increment;
				return c;
			}

			@Override
			public void remove() {
				// Ignore
			}

		};
	}


	public float getStart() {
		return start;
	}

	public float getEnd() {
		return end;
	}

	public float getIncrement() {
		return increment;
	}


}
