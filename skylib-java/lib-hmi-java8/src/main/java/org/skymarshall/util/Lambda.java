package org.skymarshall.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Lambda {

	public static final FunctionWithException<?, ?, ?> FUNCTION_IDENTITY = new FunctionWithException<Object, Object, RuntimeException>() {

		@Override
		public Object apply(final Object value) {
			return value;
		}

	};

	public static <T, E extends Exception> FunctionWithException<T, T, E> identity() {
		return (FunctionWithException<T, T, E>) FUNCTION_IDENTITY;
	}

	public static <T> Consumer<T> nothingConsumer() {
		return new Consumer<T>() {
			@Override
			public void accept(final T t) {
			}
		};
	}

	public static <T, U> BiConsumer<T, U> nothingBiConsumer() {
		return new BiConsumer<T, U>() {
			@Override
			public void accept(final T t, final U u) {
			}
		};
	}

}
