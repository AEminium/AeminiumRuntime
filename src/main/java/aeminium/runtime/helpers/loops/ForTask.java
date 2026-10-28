package aeminium.runtime.helpers.loops;

import java.util.ArrayList;
import java.util.List;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Configuration;

public class ForTask {

	public static int PPS = Configuration.getProperty(ForTask.class, "LazyBinarySplittingPPS", 1);


	public static <T> Task createFor(Runtime rt, final Iterable<T> collection,
			final ForBody<T> forBody, final short hints) {
		// Initial Copy
		final ArrayList<T> objects = new ArrayList<T>();
		for (T o : collection) {
			objects.add(o);
		}
		return createFor(rt, objects, forBody, hints);
	}

	public static <T> Task createFor(Runtime rt, final List<T> collection,
			final ForBody<T> forBody, final short hints) {
		return rt.createNonBlockingTask(createLazyBinarySplitting(0, collection.size(), collection, forBody, hints), Runtime.NO_HINTS);
	}

	public static <T> Body createLazyBinarySplitting(final int start, final int end, final List<T> collection, final ForBody<T> forBody, final short hints) {
		return new Body() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				int bottom = start;
				int top = end;

				while (bottom < top) {
					boolean shouldCheck = (bottom % PPS == 0);
					if (shouldCheck && (top-bottom > PPS) && rt.parallelize(current)) {
						int half = (top - bottom)/2 + bottom;
						Task otherHalf = rt.createNonBlockingTask(createLazyBinarySplitting(half, top, collection, forBody, hints), (short) (hints | Hints.LOOPS));
						rt.schedule(otherHalf, current, Runtime.NO_DEPS);
						top = half;
					} else {
						forBody.iterate(collection.get(bottom), rt, current);
						bottom ++;
					}
				}

			}
		};
	}

	public static Task createFor(Runtime rt, final Range collection,
			final ForBody<Integer> forBody, final short hints) {
		return rt.createNonBlockingTask(createLazyBinarySplitting(collection.getStart(), collection.getEnd(), collection.getIncrement(), forBody, hints), Runtime.NO_HINTS);
	}

	public static Body createLazyBinarySplitting(final int start, final int end, final int increment, final ForBody<Integer> forBody, final short hints) {
		return new Body() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				int bottom = start;
				int top = end;

				while (bottom < top) {
					boolean shouldCheck = (bottom % PPS == 0);
					if (shouldCheck && (top-bottom > PPS) && rt.parallelize(current)) {
						int half = (top - bottom)/2 + bottom;
						Task otherHalf = rt.createNonBlockingTask(createLazyBinarySplitting(half, top, increment, forBody, hints), (short) (hints | Hints.LOOPS));
						rt.schedule(otherHalf, current, Runtime.NO_DEPS);
						top = half;
					} else {
						forBody.iterate(bottom, rt, current);
						bottom += increment;
					}
				}

			}
		};
	}
}
