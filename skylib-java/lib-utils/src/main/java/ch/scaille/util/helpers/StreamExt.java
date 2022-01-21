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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

	public class MultiCollectionIterator<R, T> implements Iterator<T> {

		private final Function<R, Iterator<T>> loader;
		private final Iterator<R> rootsListIter;
		private Iterator<T> currentElementsIter = null;
		private Iterator<T> nextElementsIter = Collections.emptyIterator();

		public MultiCollectionIterator(final List<R> roots, Function<R, Iterator<T>> loader) {
			this.loader = loader;
			rootsListIter = roots.iterator();
		}

		@Override
		public boolean hasNext() {
			if (currentElementsIter == null || !currentElementsIter.hasNext()) {
				load();
			}
			return currentElementsIter.hasNext();
		}

		@Override
		public T next() {
			if (currentElementsIter == null) {
				throw new IllegalStateException("next() was not called");
			}
			return currentElementsIter.next();
		}

		private void load() {
			if (currentElementsIter == null) {
				currentElementsIter = loader.apply(rootsListIter.next());
			} else {
				currentElementsIter = nextElementsIter;
			}
			if (rootsListIter.hasNext()) {
				do {
					nextElementsIter = loader.apply(rootsListIter.next());
				} while (!nextElementsIter.hasNext() && rootsListIter.hasNext());
			} else {
				nextElementsIter = Collections.emptyIterator();
			}
		}
	}

	public static <R, T> Stream<T> multiCollection(final List<R> roots, Function<R, Iterator<T>> loader) {
		return StreamSupport
				.stream(Spliterators.<T>spliteratorUnknownSize(new MultiCollectionIterator<>(roots, loader), 0), false);
	}
}
