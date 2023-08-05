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

	@FunctionalInterface
	public interface BiFunctionWithException<T, U, R, E extends Throwable> {
		R apply(T value, U value2) throws E;
	}

	public static final FunctionWithException<?, ?, ?> FUNCTION_IDENTITY = v -> v;

	public static <T, E extends Exception> FunctionWithException<T, T, E> identity() {
		return (FunctionWithException<T, T, E>) FUNCTION_IDENTITY;
	}

	public static <T, E extends Exception, R> BiFunction<T, E, R> raise(
			BiFunction<T, E, RuntimeException> transformer) {
		return (t, e) -> {
			throw transformer.apply(t, e);
		};
	}

	private static Consumer<Exception> exceptionHandler = e -> { throw new IllegalStateException(e.getMessage(), e); };

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

	public static <E extends Exception> Runnable uncheckR(RunnableWithException<E> call) {
		return uncheckR(call, exceptionHandler);
	}

	public static <E extends Exception> Runnable uncheckR(RunnableWithException<E> call,
			final Consumer<? super E> exceptionHandler) {
		return () -> {
			try {
				call.execute();
			} catch (final Exception ex) {
				exceptionHandler.accept((E) ex);
			}
		};
	}

	public static <T, E extends Exception> Consumer<T> uncheckC(ConsumerWithException<T, E> call) {
		return uncheckC(call, exceptionHandler);
	}

	public static <T, E extends Exception> Consumer<T> uncheckC(ConsumerWithException<T, E> call,
			final Consumer<? super E> exceptionHandler) {
		return c -> {
			try {
				call.accept(c);
			} catch (final Exception ex) {
				exceptionHandler.accept((E) ex);
			}
		};
	}

	public static <T, R, E extends Exception> Function<T, R> uncheckF(final FunctionWithException<T, R, E> call) {
		return uncheckF(call, (m, e) -> defaultExceptionHandler(e));
	}

	public static <T, R, E extends Exception> Function<T, R> uncheckF(final FunctionWithException<T, R, E> call,
			final BiFunction<T, ? super E, R> exceptionHandler) {
		return t -> {
			try {
				return call.apply(t);
			} catch (final Exception ex) {
				return exceptionHandler.apply(t, (E) ex);
			}
		};
	}

	public static <T, U, R, E extends Exception> BiFunction<T, U, R> uncheckF2(
			final BiFunctionWithException<T, U, R, E> call) {
		return uncheckF2(call, LambdaExt::defaultExceptionHandler);
	}

	public static <T, U, R, E extends Exception> BiFunction<T, U, R> uncheckF2(
			final BiFunctionWithException<T, U, R, E> call, final Function<? super E, R> exceptionHandler) {
		return (t, u) -> {
			try {
				return call.apply(t, u);
			} catch (final Exception ex) {
				return exceptionHandler.apply((E) ex);
			}
		};
	}
	
	public static <T, E extends Exception> T uncheckM(final SupplierWithException<T, E> call) {
		return uncheckM(call, LambdaExt::defaultExceptionHandler);
	}

	public static <T, E extends Exception> T uncheckM(final SupplierWithException<T, E> call,
			final Function<? super E, T> exceptionHandler) {
		try {
			return call.execute();
		} catch (final Exception ex) {
			try {
				return exceptionHandler.apply((E) ex);
			} catch (ClassCastException c) {
				// not compatible
				if (ex instanceof RuntimeException) {
					throw (RuntimeException)ex;
				} else {
					throw new RuntimeException(ex);
				}
			}
		}
	}

	public static <E extends Exception> void uncheckM(RunnableWithException<E> call) {
		uncheckR(call).run();
	}
	
	public static <E extends Exception> void uncheckM(RunnableWithException<E> call,
			final Consumer<? super E> exceptionHandle) {
		uncheckR(call, exceptionHandler).run();
	}
}
