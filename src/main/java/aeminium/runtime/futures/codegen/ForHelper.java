package aeminium.runtime.futures.codegen;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.futures.Future;
import aeminium.runtime.futures.FutureWrapper;
import aeminium.runtime.futures.HollowFuture;
import aeminium.runtime.futures.RuntimeManager;
import aeminium.runtime.implementations.Configuration;

public class ForHelper {

	public static boolean binarySplit = Configuration.getProperty(ForHelper.class, "BinarySplitting", false);
	public static boolean compilerRange = Configuration.getProperty(ForHelper.class, "CompilerRange", true);
	public static int PPS = Configuration.getProperty(ForHelper.class, "PPS", 3);

	public static BiFunction<Integer, Integer, Integer> intSum = (t1, t2) -> t1 + t2;
	public static BiFunction<Long, Long, Long> longSum = (t1, t2) -> t1 + t2;
	public static BiFunction<Float, Float, Float> floatSum = (t1, t2) -> t1 + t2;
	public static BiFunction<Double, Double, Double> doubleSum = (t1, t2) -> t1 + t2;

	public static void forContinuousInteger(int start, int end, Function<Integer, Void> fun, short hint, int units) {
		if (!compilerRange) {
			forContinuousInteger(start, end, fun, hint);
			return;
		}
		final int nTasks = Math.min(units, 2*java.lang.Runtime.getRuntime().availableProcessors());
		Task[] tasks = new Task[nTasks];
		final int span = (int) Math.ceil( (end - start) / ((double) nTasks));
		final int top = end;
		for (int i=0; i<nTasks; i++) {
			final int j = i;
			tasks[i] = RuntimeManager.rt.createNonBlockingTask(new Body() {
				@Override
				public void execute(Runtime rt, Task current) throws Exception {
					for (int i = j*span; i < (j+1)*span && i < top; i++) {
						fun.apply(i);
					}
				}
			}, hint);
			RuntimeManager.rt.schedule(tasks[i], Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		for (Task t : tasks) {
			t.getResult();
		}
	}

	public static void forContinuousLong(long start, long end, Function<Long, Void> fun, short hint, int units) {
		if (!compilerRange) {
			forContinuousLong(start, end, fun, hint);
			return;
		}
		final int nTasks = Math.min(units, 2*java.lang.Runtime.getRuntime().availableProcessors());
		Task[] tasks = new Task[nTasks];
		final long span = (int) Math.ceil( (end - start) / ((double) nTasks));
		final long top = end;
		for (int i=0; i<nTasks; i++) {
			final int j = i;
			tasks[i] = RuntimeManager.rt.createNonBlockingTask(new Body() {
				@Override
				public void execute(Runtime rt, Task current) throws Exception {
					for (long i = j*span; i < (j+1)*span && i < top; i++) {
						fun.apply(i);
					}
				}
			}, hint);
			RuntimeManager.rt.schedule(tasks[i], Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		for (Task t : tasks) {
			t.getResult();
		}
	}

	public interface ReduceBody<T> extends Body {
		public T getAcc();
	}

	@SuppressWarnings("unchecked")
	public static <T> HollowFuture<T> forContinuousIntegerReduce1(int start, int end, Function<Integer, T> fun, BiFunction<T, T, T> reduce, short hint, int units) {
		if (!compilerRange) {
			return forContinuousIntegerReduce1(start, end, fun, reduce, hint);
		}
		final int nTasks = Math.min(units, 2*java.lang.Runtime.getRuntime().availableProcessors());
		final Task[] tasks = new Task[nTasks];
		final ReduceBody<T>[] bodies = new ReduceBody[nTasks];
		final int span = (int) Math.ceil( (end - start) / ((double) nTasks));
		final int top = end;
		for (int i=0; i<nTasks; i++) {
			final int j = i;
			bodies[i] = new ReduceBody<T>() {
				private T acc = null;
				public T getAcc() {
					return acc;
				}
				@Override
				public void execute(Runtime rt, Task current) throws Exception {
					for (int i = j*span; i < (j+1)*span && i < top; i++) {
						T curr = fun.apply(i);
						if (acc == null) acc = curr; else acc = reduce.apply(acc, curr);
					}
				}
			};
			tasks[i] = RuntimeManager.rt.createNonBlockingTask(bodies[i], hint);
			RuntimeManager.rt.schedule(tasks[i], Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		return new Future<T>( (t) -> {
			T acc = null;
			for (int i=0; i<nTasks; i++) {
				tasks[i].getResult();
				T curr = bodies[i].getAcc();
				if (acc == null) acc = curr; else acc = reduce.apply(acc, curr);
			}
			return acc;
		});
	}

	@SuppressWarnings("unchecked")
	public static <T> HollowFuture<T> forContinuousLongReduce1(long start, long end, Function<Long, T> fun, BiFunction<T, T, T> reduce, short hint, int units) {
		if (!compilerRange) {
			return forContinuousLongReduce1(start, end, fun, reduce, hint);
		}
		final int nTasks = Math.min(units, 2*java.lang.Runtime.getRuntime().availableProcessors());
		final Task[] tasks = new Task[nTasks];
		final ReduceBody<T>[] bodies = new ReduceBody[nTasks];
		final long span = (int) Math.ceil( (end - start) / ((double) nTasks));
		final long top = end;
		for (int i=0; i<nTasks; i++) {
			final int j = i;
			bodies[i] = new ReduceBody<T>() {
				private T acc = null;
				public T getAcc() {
					return acc;
				}
				@Override
				public void execute(Runtime rt, Task current) throws Exception {
					for (long i = j*span; i < (j+1)*span && i < top; i++) {
						T curr = fun.apply(i);
						if (acc == null) acc = curr; else acc = reduce.apply(acc, curr);
					}
				}
			};
			tasks[i] = RuntimeManager.rt.createNonBlockingTask(bodies[i], hint);
			RuntimeManager.rt.schedule(tasks[i], Runtime.NO_PARENT, Runtime.NO_DEPS);
		}
		return new Future<T>( (t) -> {
			T acc = null;
			for (int i=0; i<nTasks; i++) {
				tasks[i].getResult();
				T curr = bodies[i].getAcc();
				if (acc == null) acc = curr; else acc = reduce.apply(acc, curr);
			}
			return acc;
		});
	}


	public static void forContinuousInteger(int start, int end, Function<Integer, Void> fun, short hint) {
		if (start == end) return;
		if (RuntimeManager.shouldSeq()) {
			for (int i=start; i<end; i++) {
				fun.apply(i);
			}
			return;
		}
		Body b = forContinuousIntegerBody(start, end, fun, hint);
		Task current = RuntimeManager.rt.createNonBlockingTask(b,
				(short) (Hints.RECURSION | Hints.LOOPS));
		RuntimeManager.rt.schedule(current, Runtime.NO_PARENT, Runtime.NO_DEPS);
		current.getResult();
	}

	public static void forContinuousLong(long  start, long end, Function<Long, Void> fun, short hint) {
		if (start == end) return;
		if (!RuntimeManager.rt.parallelize(null)) {
			for (long i=start; i<end; i++) {
				fun.apply(i);
			}
			return;
		}
		Body b = forContinuousLongBody(start, end, fun, hint);
		Task current = RuntimeManager.rt.createNonBlockingTask(b,
				(short) (Hints.RECURSION | Hints.LOOPS));
		RuntimeManager.rt.schedule(current, Runtime.NO_PARENT, Runtime.NO_DEPS);
		current.getResult();
	}


	public static <T> HollowFuture<T> forContinuousIntegerReduce1(int start, int end, Function<Integer, T> fun, BiFunction<T, T, T> reduce, short hint) {
		Body b = forContinuousIntegerReduce1Body(start, end, fun, reduce, hint);
		Task current = RuntimeManager.rt.createNonBlockingTask(b,
				(short) (Hints.RECURSION | Hints.LOOPS));
		RuntimeManager.rt.schedule(current, Runtime.NO_PARENT, Runtime.NO_DEPS);
		return new FutureWrapper<T>(current);
	}

	public static <T> HollowFuture<T> forContinuousLongReduce1(long start, long end, Function<Long, T> fun, BiFunction<T, T, T> reduce, short hint) {
		Body b = forContinuousLongReduce1Body(start, end, fun, reduce, hint);
		Task current = RuntimeManager.rt.createNonBlockingTask(b,
				(short) (Hints.RECURSION | Hints.LOOPS));
		RuntimeManager.rt.schedule(current, Runtime.NO_PARENT, Runtime.NO_DEPS);
		return new FutureWrapper<T>(current);
	}

	public static Body forContinuousIntegerBody(final int start, final int end,
			Function<Integer, Void> fun, short hint) {
		return new Body() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				if (start == end) return;
				if (end-start < ((Hints.check(hint, Hints.SMALL)) ? 10000 : 1)) {
					seqForInteger(start, end, fun);
				} else  if (binarySplit) {
					if (rt.parallelize(current) && end-start >= 2) {
						int half = (end - start)/2 + start;
						Task h1 = rt.createNonBlockingTask(forContinuousIntegerBody(start, half, fun, hint), Hints.LOOPS);
						Task h2 = rt.createNonBlockingTask(forContinuousIntegerBody(half, end, fun, hint), Hints.LOOPS);
						rt.schedule(h1, Runtime.NO_PARENT, Runtime.NO_DEPS);
						rt.schedule(h2, Runtime.NO_PARENT, Runtime.NO_DEPS);
						h1.getResult();
						h2.getResult();
					} else {
						seqForInteger(start, end, fun);
					}
				} else {
					ArrayList<Task> children = new ArrayList<Task>();
					int bottom = start, top = end, pps = ForHelper.PPS * ((Hints.check(hint, Hints.SMALL)) ? 100 : 1);
					while (bottom < top) {
						if (bottom % pps == 0 && top-bottom > 1 && rt.parallelize(current)) {
							int half = (top - bottom)/2 + bottom;
							Task otherHalf = rt.createNonBlockingTask(forContinuousIntegerBody(half, top, fun, hint), Hints.LOOPS);
							rt.schedule(otherHalf, current, Runtime.NO_DEPS);
							children.add(otherHalf);
							top = half;
						} else {
							fun.apply(bottom++);
						}
					}
					for (Task child : children)
						child.getResult();
				}
			}
		};
	}

	public static Body forContinuousLongBody(final long start, final long end,
			Function<Long, Void> fun, short hint) {
		return new Body() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				if (start == end) return;
				if (end-start < ((Hints.check(hint, Hints.SMALL)) ? 10000 : 1)) {
					seqForLong(start, end, fun);
				} else  if (binarySplit) {
					if (rt.parallelize(current) && end-start >= 2) {
						long half = (end - start)/2 + start;
						Task h1 = rt.createNonBlockingTask(forContinuousLongBody(start, half, fun, hint), Hints.LOOPS);
						Task h2 = rt.createNonBlockingTask(forContinuousLongBody(half, end, fun, hint), Hints.LOOPS);
						rt.schedule(h1, Runtime.NO_PARENT, Runtime.NO_DEPS);
						rt.schedule(h2, Runtime.NO_PARENT, Runtime.NO_DEPS);
						h1.getResult();
						h2.getResult();
					} else {
						seqForLong(start, end, fun);
					}
				} else {
					ArrayList<Task> children = new ArrayList<Task>();
					long bottom = start, top = end, pps = ForHelper.PPS * ((Hints.check(hint, Hints.SMALL)) ? 100 : 1);
					while (bottom < top) {
						if (bottom % pps == 0 && top-bottom > 1 && rt.parallelize(current)) {
							long half = (top - bottom)/2 + bottom;
							Task otherHalf = rt.createNonBlockingTask(forContinuousLongBody(half, top, fun, hint), Hints.LOOPS);
							rt.schedule(otherHalf, current, Runtime.NO_DEPS);
							children.add(otherHalf);
							top = half;
						} else {
							fun.apply(bottom++);
						}
					}
					for (Task child : children)
						child.getResult();
				}
			}
		};
	}

	public static <T> BodyWithField<T> forContinuousIntegerReduce1Body(final int start, final int end,
			Function<Integer, T> fun, BiFunction<T, T, T> reduce, short hint) {
		return new BodyWithField<T>() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				if (start == end) return;
				if (end-start < ((Hints.check(hint, Hints.SMALL)) ? 10000 : 1)) {
					field = seqForInteger(start, end, fun, reduce);
				} else  if (binarySplit) {
					if (rt.parallelize(current) && end-start >= 2) {
						int half = (end - start)/2 + start;
						BodyWithField<T> b1 = forContinuousIntegerReduce1Body(start, half, fun, reduce, hint);
						BodyWithField<T> b2 = forContinuousIntegerReduce1Body(half, end, fun, reduce, hint);
						Task h1 = rt.createNonBlockingTask(b1, Hints.LOOPS);
						Task h2 = rt.createNonBlockingTask(b2, Hints.LOOPS);
						rt.schedule(h1, Runtime.NO_PARENT, Runtime.NO_DEPS);
						rt.schedule(h2, Runtime.NO_PARENT, Runtime.NO_DEPS);
						h1.getResult();
						h2.getResult();
						field = reduce.apply(b1.field, b2.field);
					} else {
						field = seqForInteger(start, end, fun, reduce);
					}
				} else {
					ArrayList<Task> children = new ArrayList<Task>();
					ArrayList<BodyWithField<T>> bodies = new ArrayList<BodyWithField<T>>();
					int bottom = start, top = end, pps = ForHelper.PPS * ((Hints.check(hint, Hints.SMALL)) ? 100 : 1);
					while (bottom < top) {
						if (bottom % pps == 0 && top-bottom > 1 && rt.parallelize(current)) {
							int half = (top - bottom)/2 + bottom;
							BodyWithField<T> body = forContinuousIntegerReduce1Body(half, top, fun, reduce, hint);
							Task otherHalf = rt.createNonBlockingTask(body, Hints.LOOPS);
							rt.schedule(otherHalf, current, Runtime.NO_DEPS);
							children.add(otherHalf);
							bodies.add(body);
							top = half;
						} else {
							T res = fun.apply(bottom++);
							field = (field == null) ? res : reduce.apply(field, res);
						}
					}
					for (Task child : children) {
						child.getResult();
					}
					for (BodyWithField<T> b : bodies) {
						T r = (T) b.field;
						field = (field == null) ? r : reduce.apply(field, r);
					}
				}
				current.setResult(field);
			}
		};
	}

	public static <T> BodyWithField<T> forContinuousLongReduce1Body(final long start, final long end,
			Function<Long, T> fun, BiFunction<T, T, T> reduce, short hint) {
		return new BodyWithField<T>() {
			@Override
			public void execute(Runtime rt, Task current) throws Exception {
				if (start == end) return;
				if (end-start < ((Hints.check(hint, Hints.SMALL)) ? 10000 : 1)) {
					field = seqForLong(start, end, fun, reduce);
				} else  if (binarySplit) {
					if (rt.parallelize(current) && end-start >= 2) {
						long half = (end - start)/2 + start;
						BodyWithField<T> b1 = forContinuousLongReduce1Body(start, half, fun, reduce, hint);
						BodyWithField<T> b2 = forContinuousLongReduce1Body(half, end, fun, reduce, hint);
						Task h1 = rt.createNonBlockingTask(b1, Hints.LOOPS);
						Task h2 = rt.createNonBlockingTask(b2, Hints.LOOPS);
						rt.schedule(h1, Runtime.NO_PARENT, Runtime.NO_DEPS);
						rt.schedule(h2, Runtime.NO_PARENT, Runtime.NO_DEPS);
						h1.getResult();
						h2.getResult();
						field = reduce.apply(b1.field, b2.field);
					} else {
						field = seqForLong(start, end, fun, reduce);
					}
				} else {
					ArrayList<Task> children = new ArrayList<Task>();
					ArrayList<BodyWithField<T>> bodies = new ArrayList<BodyWithField<T>>();
					long bottom = start, top = end, pps = ForHelper.PPS * ((Hints.check(hint, Hints.SMALL)) ? 100 : 1);
					while (bottom < top) {
						if (bottom % pps == 0 && top-bottom > 1 && rt.parallelize(current)) {
							long half = (top - bottom)/2 + bottom;
							BodyWithField<T> body = forContinuousLongReduce1Body(half, top, fun, reduce, hint);
							Task otherHalf = rt.createNonBlockingTask(body, Hints.LOOPS);
							rt.schedule(otherHalf, current, Runtime.NO_DEPS);
							children.add(otherHalf);
							bodies.add(body);
							top = half;
						} else {
							T res = fun.apply(bottom++);
							field = (field == null) ? res : reduce.apply(field, res);
						}
					}
					for (Task child : children) {
						child.getResult();
					}
					for (BodyWithField<T> b : bodies) {
						T r = (T) b.field;
						field = (field == null) ? r : reduce.apply(field, r);
					}
				}
				current.setResult(field);
			}
		};
	}


	private static void seqForInteger(final int start, final int end,
			Function<Integer, Void> fun) {
		for (int i=start; i<end; i++) {
			fun.apply(i);
		}
	}

	private static void seqForLong(final long start, final long end,
			Function<Long, Void> fun) {
		for (long i=start; i<end; i++) {
			fun.apply(i);
		}
	}

	private static <T> T seqForInteger(final int start, final int end,
			Function<Integer, T> fun, BiFunction<T, T, T> reduce) {
		T acc = fun.apply(start);
		for (int i=start+1; i<end; i++) {
			acc = reduce.apply(acc, fun.apply(i));
		}
		return acc;
	}

	private static <T> T seqForLong(final long start, final long end,
			Function<Long, T> fun, BiFunction<T, T, T> reduce) {
		T acc = fun.apply(start);
		for (long i=start+1; i<end; i++) {
			acc = reduce.apply(acc, fun.apply(i));
		}
		return acc;
	}

	public static void main(String[] args) {
		RuntimeManager.init();
		ForHelper.forContinuousInteger(0, 10, (Integer t) -> {
			System.out.println(t);
			return null;
		}, Hints.NO_HINTS);

		HollowFuture<Integer> t = ForHelper.forContinuousIntegerReduce1(0, 10, (Integer i) -> {
			return 1;
		}, ForHelper.intSum, Hints.SMALL);
		System.out.println("Sum:" + t.get());
		RuntimeManager.shutdown();

	}
}
