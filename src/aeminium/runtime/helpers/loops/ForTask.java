package aeminium.runtime.helpers.loops;

import java.util.ArrayList;
import java.util.List;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Configuration;

public class ForTask {

	public static int PARALLELISM_SIZE = Configuration.getProperty(ForTask.class, "ForParallelismSize", 64);

	/* This version is here for backwards compability. */
	public static <T> Task createFor(Runtime rt, final Iterable<T> collection,
			final ForBody<T> forBody) {
		return createFor(rt, collection, forBody, Runtime.NO_HINTS);
	}
	
	public static <T> Task createFor(Runtime rt, final Iterable<T> collection,
			final ForBody<T> forBody, final short hints) {

		return rt.createNonBlockingTask(new Body() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				// Initial Copy
				final ArrayList<T> objects = new ArrayList<T>();
				for (T o : collection) {
					objects.add(o);
				}

				int blockSize = objects.size() / PARALLELISM_SIZE;
				if (blockSize <= 0)
					blockSize = 1;
				for (int i = 0; i < objects.size(); i += blockSize) {
					final List<T> iterationSet = objects.subList(i,
							Math.min(objects.size(), i + blockSize));
					Task iterationBulk = rt.createNonBlockingTask(new Body() {

						@Override
						public void execute(Runtime rt, Task current)
								throws Exception {
							for (T obj : iterationSet) {
								forBody.iterate(obj, rt, current);
							}
						}

					}, (short) (Hints.LOOPS | hints));
					rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
				}
			}

		}, Runtime.NO_HINTS);
	}

	public static <T> Task createFor(Runtime rt, final List<T> collection,
			final ForBody<T> forBody, final short hints) {
		return rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				int fullSize = collection.size();
				if (fullSize > PARALLELISM_SIZE) {
					int blocks = PARALLELISM_SIZE;
					int blockSize = fullSize / PARALLELISM_SIZE;
					if (fullSize % PARALLELISM_SIZE != 0) blocks++;
					for (int i = 0; i < blocks; i += 1) {
						final int blockStart = i * blockSize;
						final int blockEnd = (i + 1) * blockSize;
						Task iterationBulk = rt.createNonBlockingTask(
								new Body() {
									@Override
									public void execute(Runtime rt, Task current)
											throws Exception {
										for (int s = blockStart; s < blockEnd && s < collection.size(); s += 1) {
											forBody.iterate(collection.get(s), rt, current);
										}
									}

								}, (short) (Hints.LOOPS | hints));
						rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
					}
				} else {
					for (int i = 0; i < collection.size(); i += 1) {
						forBody.iterate(collection.get(i), rt, current);
					}
				}
			}

		}, Hints.LOOPS);
	}

	public static Task createFor(Runtime rt, final LongRange range,
			final ForBody<Long> forBody, final short hints) {
		return rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				long fullSize = (range.getEnd() - range.getStart())
						/ range.getIncrement();
				if (fullSize > PARALLELISM_SIZE) {
					int blocks = PARALLELISM_SIZE;
					long blockSize = fullSize / PARALLELISM_SIZE;
					if (fullSize % PARALLELISM_SIZE != 0) blocks++;
					for (long i = 0; i < blocks; i += 1) {
						final long blockStart = i * blockSize
								* range.getIncrement();
						final long blockEnd = (i + 1) * blockSize
								* range.getIncrement();
						Task iterationBulk = rt.createNonBlockingTask(
								new Body() {
									@Override
									public void execute(Runtime rt, Task current)
											throws Exception {
										for (long s = blockStart; s < blockEnd && s < range.getEnd(); s += range
												.getIncrement()) {
											forBody.iterate(s, rt, current);
										}
									}

								}, (short) (Hints.LOOPS | hints));
						rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
					}
				} else {
					for (long i = range.getStart(); i < range.getEnd(); i += range
							.getIncrement()) {
						forBody.iterate(i, rt, current);
					}
				}
			}

		}, Hints.LOOPS);
	}

	public static Task createFor(Runtime rt, final Range range,
			final ForBody<Integer> forBody, final short hints) {
		return rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				int fullSize = (range.getEnd() - range.getStart())
						/ range.getIncrement();
				if (fullSize > PARALLELISM_SIZE) {
					int blocks = PARALLELISM_SIZE;
					int blockSize = fullSize / PARALLELISM_SIZE;
					for (int i = 0; i < blocks; i += 1) {
						final int blockStart = i * blockSize
								* range.getIncrement();
						final int blockEnd = (i < blocks-1) ? ((i + 1) * blockSize
								* range.getIncrement()) : range.getEnd();
						Task iterationBulk = rt.createNonBlockingTask(
								new Body() {
									@Override
									public void execute(Runtime rt, Task current)
											throws Exception {
										for (int s = blockStart; s < blockEnd && s < range.getEnd(); s += range
												.getIncrement()) {
											forBody.iterate(s, rt, current);
										}
									}

								}, (short) (Hints.LOOPS | hints));
						rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
					}
				} else {
					for (int i = range.getStart(); i < range.getEnd(); i += range
							.getIncrement()) {
						forBody.iterate(i, rt, current);
					}
				}
			}

		}, Hints.LOOPS);
	}
	
	
	public static Task createFor(Runtime rt, final FloatRange range,
			final ForBody<Float> forBody, final short hints) {
		return rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				float fullSize = (range.getEnd() - range.getStart())
						/ range.getIncrement();
				if (fullSize > PARALLELISM_SIZE) {
					int blocks = PARALLELISM_SIZE;
					float blockSize = fullSize / PARALLELISM_SIZE;
					for (int i = 0; i < blocks; i += 1) {
						final float blockStart = i * blockSize
								* range.getIncrement();
						final float blockEnd = (i < blocks-1) ? ((i + 1) * blockSize
								* range.getIncrement()) : range.getEnd();
						Task iterationBulk = rt.createNonBlockingTask(
								new Body() {
									@Override
									public void execute(Runtime rt, Task current)
											throws Exception {
										for (float s = blockStart; s < blockEnd && s < range.getEnd(); s += range
												.getIncrement()) {
											forBody.iterate(s, rt, current);
										}
									}

								}, (short) (Hints.LOOPS | hints));
						rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
					}
				} else {
					for (float i = range.getStart(); i < range.getEnd(); i += range
							.getIncrement()) {
						forBody.iterate(i, rt, current);
					}
				}
			}

		}, Runtime.NO_HINTS);
	}
	
	public static Task createFor(Runtime rt, final DoubleRange range,
			final ForBody<Double> forBody, final short hints) {
		return rt.createNonBlockingTask(new Body() {

			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				double fullSize = (range.getEnd() - range.getStart())
						/ range.getIncrement();
				if (fullSize > PARALLELISM_SIZE) {
					int blocks = PARALLELISM_SIZE;
					double blockSize = fullSize / PARALLELISM_SIZE;
					for (int i = 0; i < blocks; i += 1) {
						final double blockStart = i * blockSize
								* range.getIncrement();
						final double blockEnd = (i < blocks-1) ? ((i + 1) * blockSize
								* range.getIncrement()) : range.getEnd();
						Task iterationBulk = rt.createNonBlockingTask(
								new Body() {
									@Override
									public void execute(Runtime rt, Task current)
											throws Exception {
										for (double s = blockStart; s < blockEnd && s < range.getEnd(); s += range
												.getIncrement()) {
											forBody.iterate(s, rt, current);
										}
									}

								}, (short) (Hints.LOOPS | hints));
						rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
					}
				} else {
					for (double i = range.getStart(); i < range.getEnd(); i += range
							.getIncrement()) {
						forBody.iterate(i, rt, current);
					}
				}
			}

		}, Hints.LOOPS);
	}
}
