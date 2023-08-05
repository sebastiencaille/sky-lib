package ch.scaille.util.helpers;

import java.util.function.Consumer;
import java.util.function.Function;

import ch.scaille.util.helpers.LambdaExt.RunnableWithException;
import ch.scaille.util.helpers.LambdaExt.SupplierWithException;

/**
 * Exception extension
 */
public class ExcExt {

	private ExcExt() {
	}

	public static <E extends Exception> void uncheck(RunnableWithException<E> call) {
		LambdaExt.uncheckM(call);
	}

	public static <E extends Exception> void uncheck(RunnableWithException<E> call,
			final Consumer<? super E> exceptionHandler) {
		LambdaExt.uncheckM(call, exceptionHandler);
	}

	public static <R, E extends Exception> R uncheck(SupplierWithException<R, E> call) {
		return LambdaExt.uncheckM(call);
	}

	public static <R, E extends Exception> R uncheck(SupplierWithException<R, E> call,
			final Function<? super E, R> exceptionHandler) {
		return LambdaExt.uncheckM(call, exceptionHandler);
	}

}