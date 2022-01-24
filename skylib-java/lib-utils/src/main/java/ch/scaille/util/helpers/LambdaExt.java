package ch.scaille.util.helpers;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class LambdaExt {

	private LambdaExt() {
	}

	@FunctionalInterface
	public interface RunnableWithException<E extends Exception> {
		void execute() throws E;
	}

	@FunctionalInterface
	public interface ConsumerWithException<T, E extends Exception> {
		void accept(T value) throws E;
	}

	@FunctionalInterface
	public interface SupplierWithException<T, E extends Exception> {
		T execute() throws E;
	}

	@FunctionalInterface
	public interface FunctionWithException<T, R, E extends Throwable> {
		R apply(T value) throws E;
	}

	public static final FunctionWithException<?, ?, ?> FUNCTION_IDENTITY = v -> v;

	public static <T, E extends Exception> FunctionWithException<T, T, E> identity() {
		return (FunctionWithException<T, T, E>) FUNCTION_IDENTITY;
	}
	
	private static Consumer<Exception> exceptionHandler = e -> {
		throw new IllegalStateException(e.getMessage(), e);
	};

	public static <R> R defaultExceptionHandler(Exception e) {
		exceptionHandler.accept(e);
		return null;
	}


	public static void setDefaultExceptionHandler(Consumer<Exception> anExceptionHandler) {
		exceptionHandler = anExceptionHandler;
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

	public static <E extends Exception> Runnable withExc(RunnableWithException<E> call) {
		return withExc(call, exceptionHandler);
	}

	public static <E extends Exception> Runnable withExc(RunnableWithException<E> call,
			final Consumer<? super E> exceptionHandler) {
		return () -> {
			try {
				call.execute();
			} catch (final Exception ex) {
				exceptionHandler.accept((E) ex);
			}
		};
	}

	public static <T, E extends Exception> Consumer<T> withExc(ConsumerWithException<T, E> call) {
		return withExc(call, exceptionHandler);
	}

	public static <T, E extends Exception> Consumer<T> withExc(ConsumerWithException<T, E> call,
			final Consumer<? super E> exceptionHandler) {
		return c -> {
			try {
				call.accept(c);
			} catch (final Exception ex) {
				exceptionHandler.accept((E) ex);
			}
		};
	}

	public static <T, E extends Exception> T suppWithExc(final SupplierWithException<T, E> call) {
		return suppWithExc(call, LambdaExt::defaultExceptionHandler);
	}

	public static <T, E extends Exception> T suppWithExc(final SupplierWithException<T, E> call,
			final Function<? super E, T> exceptionHandler) {
		try {
			return call.execute();
		} catch (final Exception ex) {
			return exceptionHandler.apply((E) ex);
		}
	}

	public static <T, R, E extends Exception> Function<T, R> funcWithExc(final FunctionWithException<T, R, E> call) {
		return funcWithExc(call, LambdaExt::defaultExceptionHandler);
	}

	public static <T, R, E extends Exception> Function<T, R> funcWithExc(final FunctionWithException<T, R, E> call,
			final Function<? super E, R> exceptionHandler) {
		return t -> {
			try {
				return call.apply(t);
			} catch (final Exception ex) {
				return exceptionHandler.apply((E) ex);
			}
		};
	}
}
