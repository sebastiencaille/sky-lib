package ch.scaille.javabeans.properties;

/**
 * Error container
 *
 * @author Sebastien Caille
 *
 */
public class ConversionError {
	private final AbstractProperty property;
	private final Object content;
	private final String message;

	public ConversionError(final AbstractProperty property, final String message, final Object content) {
		this.property = property;
		this.message = message;
		this.content = content;
	}

	public AbstractProperty getProperty() {
		return property;
	}

	public Object getContent() {
		return content;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return property.getName() + ": " + message;
	}
}