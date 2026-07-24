package ch.scaille.util.persistence;

import java.io.IOException;

import ch.scaille.util.helpers.LambdaExt.RunnableWithException;
import ch.scaille.util.helpers.LambdaExt.SupplierWithException;
import org.jspecify.annotations.Nullable;

public class StorageException extends IOException {

	public StorageException(String message, Exception e) {
		super(message, e);
	}

	public StorageException(String message) {
		super(message);
	}

	public static void wrap(String operation, RunnableWithException<IOException> runnable) throws StorageException {
		try {
			runnable.execute();
		} catch (IOException e) {
			throw new StorageException(operation, e);
		}
	}

	public static <T extends @Nullable Object> T wrap(String operation, SupplierWithException<T, IOException> supplier) throws StorageException {
		try {
			return supplier.execute();
		} catch (IOException e) {
			throw new StorageException(operation, e);
		}
	}

}
