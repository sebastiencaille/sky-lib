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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class LambdaExt {

	@FunctionalInterface
	public interface RunnableWithExceptions<E extends Exception, F extends Exception> {
		public void run() throws E, F;
	}

	@FunctionalInterface
	public interface RunnableWithException<E extends Exception> {
		void execute() throws E;
	}

	@FunctionalInterface
	public interface SupplierWithException<T, E extends Exception> {
		T execute() throws E;
	}

	@FunctionalInterface
	public interface FunctionWithException<T, R, E extends Throwable> {
		R apply(T value) throws E;
	}

	private LambdaExt() {

	}

	public static final FunctionWithException<?, ?, ?> FUNCTION_IDENTITY = v -> v;

	public static <T, E extends Exception> FunctionWithException<T, T, E> identity() {
		return (FunctionWithException<T, T, E>) FUNCTION_IDENTITY;
	}

	public static <T> Consumer<T> emptyConsumer() {
		return t -> {
			// empty
		};
	}

	public static <T, U, V> BiFunction<T, U, V> emptyBiFunction() {
		return (t, u) -> null;
	}

	public static <T, U> BiConsumer<T, U> emptyBiConsumer() {
		return (t, u) -> {
			// empty
		};
	}

	public static <E extends Exception> void withExc(RunnableWithException<E> r) {
		withExc(r, e -> {
			throw new IllegalStateException(e.getMessage(), e);
		});
	}

	public static <E extends Exception> void withExc(RunnableWithException<E> r, final Consumer<E> exceptionHandler) {
		try {
			r.execute();
		} catch (final Exception ex) {
			exceptionHandler.accept((E) ex);
		}
	}

	public static <T, E extends Exception> T resWithExc(final SupplierWithException<T, E> s) {
		return resWithExc(s, e -> {
			throw new IllegalStateException(e.getMessage(), e);
		});
	}

	public static <T, E extends Exception> T resWithExc(final SupplierWithException<T, E> s,
			final Function<E, T> exceptionHandler) {
		try {
			return s.execute();
		} catch (final Exception ex) {
			return exceptionHandler.apply((E) ex);
		}
	}

	
}
