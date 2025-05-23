package ch.scaille.util.helpers;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface StreamExt {

	class Single<T> {
		private final boolean allowZero;
		private T value = null;
		private int count;

		private Single(boolean allowZero) {
			this.allowZero = allowZero;
		}

		public static <T> Single<T> zeroOrOne() {
			return new Single<>(true);
		}

		public static <T> Single<T> single() {
			return new Single<>(false);
		}

		void setValue(final T newValue) {
			if (newValue == null) {
				return;
			}
			count++;
			this.value = newValue;
		}

		Single<T> combiner(Single<T> t2) {
			return this;
		}

		private boolean wrongCount() {
			return (count == 0 && !allowZero) || count > 1;
		}

		public T get() {
			if (wrongCount()) {
				throw new WrongCountException(count);
			}
			return value;
		}

		public Optional<T> optional() {
			if (wrongCount()) {
				throw new WrongCountException(count);
			}
			return Optional.of(value);
		}

		public <E extends Exception> T orElseThrow(final IntFunction<E> supplier) throws E {
			if (wrongCount()) {
				throw supplier.apply(count);
			}
			return value;
		}

		public <E extends Exception> Optional<T> optionalOrThrow(final IntFunction<E> supplier) throws E {
			if (wrongCount()) {
				throw supplier.apply(count);
			}
			return Optional.ofNullable(value);
		}
	}

	/**
	 * Collector that checks for zero or one value available
	 */
	static <E> Collector<E, ?, Single<E>> zeroOrOne() {
		return Collector.of(Single::zeroOrOne, Single::setValue, Single::combiner);
	}

	/**
	 * Collector that checks for zero or one value available
	 */
	static <E> Collector<E, ?, Single<E>> single() {
		return Collector.of(Single::single, Single::setValue, Single::combiner);
	}

	static <E> Collector<E, ?, Collection<E>> addTo(Collection<E> target) {
		return Collector.of(() -> target, Collection::add, (t, u) -> t);
	}

	static <T> void throwIfContainsNull(final Stream<T> stream) {
		stream.filter(Objects::isNull).findAny().ifPresent(t -> { 
			throw new IllegalArgumentException("No null value allowed"); 
		});
	}

	static <T> Predicate<T> notEq(final T val) {
		return v -> !Objects.equals(v, val);
	}

	static void checkContent(final Stream<?> collection, final Class<?> clazz) {
		final var mismatches = collection.filter(c -> !clazz.isInstance(c))
				.map(Object::getClass)
				.collect(Collectors.toSet());
		if (!mismatches.isEmpty()) {
			throw new IllegalArgumentException(
					"Collection has an instances of " + mismatches + ", which are not " + clazz);
		}
	}

}
