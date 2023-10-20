package ch.scaille.util.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class TupleStream<X, Y> {

	public static class Tuple<X, Y> {
		private final X x;
		private final Y y;

		public Tuple(X x, Y y) {
			this.x = x;
			this.y = y;
		}

		public X getX() {
			return x;
		}

		public Y getY() {
			return y;
		}

		@Override
		public String toString() {
			return "(" + x + ", " + y + ')';
		}

	}

	// Collectors

	public static <X, Y> Collector<X, ?, TupleStream<X, Y>> streamOf(final Function<X, Y> xToY) {
		return new CollectorFunction<>(t -> new TupleStream<>(t, xToY));
	}

	public static <X, Y> Collector<X, ?, ListStream<X, Y>> streamOfList(final Function<X, Collection<Y>> xToY) {
		return new CollectorFunction<>(t -> new ListStream<>(t, xToY));
	}

	// Mappers

	public static <X, Y> Function<X, Tuple<X, Y>> of(Function<X, Y> xToY) {
		return x -> new Tuple<>(x, xToY.apply(x));
	}

	public static <X, Y> Function<X, Stream<Tuple<X, Y>>> ofList(Function<X, Collection<Y>> xToY) {
		return x -> xToY.apply(x).stream().map(y -> new Tuple<>(x, y));
	}

	public static <X, Y> Function<X, Stream<Tuple<X, Y>>> ofStream(Function<X, Stream<Y>> xToY) {
		return x -> xToY.apply(x).map(y -> new Tuple<>(x, y));
	}

	private final Collection<X> collectionX;
	private final Function<X, Y> xToY;

	public TupleStream(final Collection<X> collectionX, final Function<X, Y> xToY) {
		this.collectionX = collectionX;
		this.xToY = xToY;
	}

	public void forEach(final BiConsumer<X, Y> consumer) {
		this.collectionX.forEach(x -> consumer.accept(x, xToY.apply(x)));
	}

	public Stream<Tuple<X, Y>> stream() {
		return this.collectionX.stream().map(x -> new Tuple<>(x, xToY.apply(x)));
	}

	public static final class ListStream<X, Y> {

		private final TupleStream<X, Collection<Y>> innerStream;

		public ListStream(final Collection<X> collectionX, final Function<X, Collection<Y>> xToY) {
			this.innerStream = new TupleStream<>(collectionX, xToY);
		}

		public void forEach(final BiConsumer<X, Y> consumer) {
			innerStream.forEach((x, c) -> c.forEach(y -> consumer.accept(x, y)));
		}

		public Stream<Tuple<X, Y>> stream() {
			return innerStream.stream().flatMap(t -> t.getY().stream().map(y -> new Tuple<>(t.getX(), y)));
		}

	}

	private static class CollectorFunction<X, R>
			implements Collector<X, List<X>, R>, BiConsumer<List<X>, X>, BinaryOperator<List<X>> {

		private Function<List<X>, R> finisher;

		public CollectorFunction(Function<List<X>, R> finisher) {
			this.finisher = finisher;
		}

		@Override
		public <V> BiFunction<List<X>, List<X>, V> andThen(final Function<? super List<X>, ? extends V> after) {
			return BinaryOperator.super.andThen(after);
		}

		@Override
		public List<X> apply(final List<X> t, final List<X> u) {
			t.addAll(u);
			return t;
		}

		@Override
		public void accept(final List<X> t, final X u) {
			t.add(u);
		}

		@Override
		public Supplier<List<X>> supplier() {
			return ArrayList::new;
		}

		@Override
		public BiConsumer<List<X>, X> accumulator() {
			return this;
		}

		@Override
		public BinaryOperator<List<X>> combiner() {
			return this;
		}

		@Override
		public Function<List<X>, R> finisher() {
			return finisher;
		}

		@Override
		public Set<Collector.Characteristics> characteristics() {
			return Collections.emptySet();
		}

	}

}
