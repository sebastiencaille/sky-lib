package ch.scaille.util.exceptions;

public class StorageException extends Exception {

	public StorageException(String message, Exception e) {
		super(message, e);
	}

	public StorageException(String message) {
		super(message);
	}

}
