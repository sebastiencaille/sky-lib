package ch.scaille.javabeans.properties;

/**
 * Error container
 *
 * @author Sebastien Caille
 *
 */
public record ConversionError(AbstractProperty property, String message, Object content) {

	@Override
	public String toString() {
		return property.getName() + ": " + message;
	}

}