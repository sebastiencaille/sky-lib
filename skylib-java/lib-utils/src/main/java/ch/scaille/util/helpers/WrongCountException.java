package ch.scaille.util.helpers;

public class WrongCountException extends RuntimeException {
	public WrongCountException(int count) {
		super("Wrong count: " + count);
	}
}