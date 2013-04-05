package aeminium.runtime.helpers.loops;

import java.util.ArrayList;
import java.util.List;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

public class ForTask {

	public static int PARALLELISM_SIZE = 32;

	public static <T> Task createFor(Runtime rt, final Iterable<T> collection,
			final ForBody<T> forBody) {

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
								forBody.iterate(obj);
							}
						}

					}, Runtime.NO_HINTS);
					rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
				}
			}

		}, Runtime.NO_HINTS);
	}

	public static Task createFor(Runtime rt, final LongRange range,
			final ForBody<Long> forBody) {
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
											forBody.iterate(s);
										}
									}

								}, Runtime.NO_HINTS);
						rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
					}
				} else {
					for (long i = range.getStart(); i < range.getEnd(); i += range
							.getIncrement()) {
						forBody.iterate(i);
					}
				}
			}

		}, Runtime.NO_HINTS);
	}

	public static Task createFor(Runtime rt, final Range range,
			final ForBody<Integer> forBody) {
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
											forBody.iterate(s);
										}
									}

								}, Runtime.NO_HINTS);
						rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
					}
				} else {
					for (int i = range.getStart(); i < range.getEnd(); i += range
							.getIncrement()) {
						forBody.iterate(i);
					}
				}
			}

		}, Runtime.NO_HINTS);
	}
	
	
	public static Task createFor(Runtime rt, final FloatRange range,
			final ForBody<Float> forBody) {
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
											forBody.iterate(s);
										}
									}

								}, Runtime.NO_HINTS);
						rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
					}
				} else {
					for (float i = range.getStart(); i < range.getEnd(); i += range
							.getIncrement()) {
						forBody.iterate(i);
					}
				}
			}

		}, Runtime.NO_HINTS);
	}
	
	public static Task createFor(Runtime rt, final DoubleRange range,
			final ForBody<Double> forBody) {
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
											forBody.iterate(s);
										}
									}

								}, Runtime.NO_HINTS);
						rt.schedule(iterationBulk, current, Runtime.NO_DEPS);
					}
				} else {
					for (double i = range.getStart(); i < range.getEnd(); i += range
							.getIncrement()) {
						forBody.iterate(i);
					}
				}
			}

		}, Runtime.NO_HINTS);
	}
}
