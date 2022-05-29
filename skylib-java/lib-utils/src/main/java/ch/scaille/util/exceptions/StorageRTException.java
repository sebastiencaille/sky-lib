package ch.scaille.util.exceptions;

public class StorageRTException extends RuntimeException {

	public StorageRTException(String message, Exception e) {
		super(message, e);
	}

	public StorageRTException(String message) {
		super(message);
	}

}
