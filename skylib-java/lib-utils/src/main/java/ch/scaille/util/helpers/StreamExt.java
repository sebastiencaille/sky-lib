/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.scaille.util.helpers;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface StreamExt {

	public static class Single<T> {
		private boolean allowZero = false;
		private Optional<T> value = Optional.empty();
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
			this.value = Optional.ofNullable(newValue);
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
			return value.orElse(null);
		}

		public Optional<T> optional() {
			if (wrongCount()) {
				throw new WrongCountException(count);
			}
			return value;
		}

		public <E extends Exception> T orElseThrow(final IntFunction<E> supplier) throws E {
			if (wrongCount()) {
				throw supplier.apply(count);
			}
			return value.orElse(null);
		}

		public <E extends Exception> Optional<T> optionalOrThrow(final IntFunction<E> supplier) throws E {
			if (wrongCount()) {
				throw supplier.apply(count);
			}
			return value;
		}
	}

	/**
	 * Collector that checks for zero or one value available
	 *
	 * @return
	 */
	public static <E> Collector<E, ?, Single<E>> zeroOrOne() {
		return Collector.of(Single::zeroOrOne, Single::setValue, Single::combiner);
	}

	/**
	 * Collector that checks for zero or one value available
	 *
	 * @return
	 */
	public static <E> Collector<E, ?, Single<E>> single() {
		return Collector.of(Single::single, Single::setValue, Single::combiner);
	}

	public static <E> Collector<E, ?, Collection<E>> addTo(Collection<E> target) {
		return Collector.of(() -> target, (t, v) -> t.add(v), (t, u) -> t);
	}

	public static <T> void throwIfContainsNull(final Stream<T> stream) {
		stream.filter(Objects::isNull).findAny().ifPresent(t -> new IllegalArgumentException("No null value allowed"));
	}

	public static <T> Predicate<T> notEq(final T val) {
		return v -> !Objects.equals(v, val);
	}


	public static void checkContent(final Stream<?> collection, final Class<?> clazz) {
		Set<?> mismatches = collection.filter(c -> !clazz.isInstance(c)).map(Object::getClass)
				.collect(Collectors.toSet());
		if (!mismatches.isEmpty()) {
			throw new IllegalArgumentException(
					"Collection has an instances of " + mismatches + ", which are not " + clazz);
		}
	}
}
