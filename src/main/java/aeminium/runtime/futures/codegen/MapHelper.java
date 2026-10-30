package aeminium.runtime.futures.codegen;

import java.util.ArrayList;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import aeminium.runtime.futures.Future;
import aeminium.runtime.futures.RuntimeManager;

public class MapHelper {

	public static <T, R> Stream<R> map(Stream<T> boxed, Function<T,R> func) {
		Spliterator<T> current = boxed.spliterator();
		return MapHelper.map(current, func);
	}

	public static <T, R> Stream<R> map(Spliterator<T> sp, Function<T,R> func) {
		ArrayList<Future<Stream<R>>> results = new ArrayList<Future<Stream<R>>>();
		Spliterator<T> second;
		while (RuntimeManager.rt.parallelize(null)) {
			second = sp.trySplit();
			if (second == null) break;
			Spliterator<T> other = second;
			results.add(0, new Future<Stream<R>>((t) -> MapHelper.map(other, func)));
		}

		Builder<R> b = Stream.builder();
		while ( sp.tryAdvance((t) -> b.accept(func.apply(t))) ) {}

		if (results.size() == 0) return b.build();
		return Stream.concat(b.build(), merge(results));
	}

	protected static <T, R> Stream<R> merge(ArrayList<Future<Stream<R>>> list) {
		Builder<R> b = Stream.builder();
		for (Future<Stream<R>> f : list) {
			f.get().forEach((i) -> b.accept(i));
		}
		return b.build();
	}

}
