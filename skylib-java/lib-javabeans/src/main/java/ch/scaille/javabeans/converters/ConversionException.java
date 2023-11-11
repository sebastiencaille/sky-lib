package ch.scaille.javabeans.converters;

public class ConversionException extends Exception {

	private static final long serialVersionUID = -4239247390457114130L;

	public ConversionException(final String message) {
		super(message);
	}

	public ConversionException(final String message, final Throwable t) {
		super(message, t);
	}

}
