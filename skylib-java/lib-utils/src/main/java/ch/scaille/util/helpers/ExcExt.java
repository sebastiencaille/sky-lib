package ch.scaille.util.helpers;

import java.util.function.Consumer;

import ch.scaille.util.helpers.LambdaExt.RunnableWithException;
import ch.scaille.util.helpers.LambdaExt.SupplierWithException;

public class ExcExt {

	private ExcExt() {
	}

	public static <E extends Exception> void uncheck(RunnableWithException<E> call) {
		LambdaExt.uncheck(call).run();
	}

	public static <E extends Exception> void uncheck(RunnableWithException<E> call,
			final Consumer<? super E> exceptionHandler) {
		LambdaExt.uncheck(call, exceptionHandler).run();
	}

	public static <R, E extends Exception> R uncheck(SupplierWithException<R, E> call) {
		return LambdaExt.uncheck(call);
	}

}