package ch.scaille.util.persistence;

import ch.scaille.util.helpers.ExcExt;
import ch.scaille.util.helpers.LambdaExt.RunnableWithException;
import ch.scaille.util.helpers.LambdaExt.SupplierWithException;

public class StorageRTException extends RuntimeException {

	public StorageRTException(String message, Exception e) {
		super(message, e);
	}

	public StorageRTException(String message) {
		super(message);
	}

	public static void uncheck(String operation, RunnableWithException<StorageException> runnable) {
		ExcExt.uncheck(runnable, e -> {
			throw new StorageRTException(operation + " failed", e);
		});
	}
	
	public static <T> T uncheck(String operation, SupplierWithException<T, StorageException> supplier) {
		return ExcExt.uncheck(supplier, e -> {
			throw new StorageRTException(operation + " failed", e);
		});
	}


}
