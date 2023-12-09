package ch.scaille.javabeans.properties;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;

/**
 * To track the current errors (validation, exceptions, ...) 
 */
public class ErrorSet implements ErrorNotifier {

	private final MapProperty<AbstractProperty, ConversionError> errors;

	private final ObjectProperty<ConversionError> lastError;

	public ErrorSet(final String name, final IPropertiesGroup support) {
		errors = new MapProperty<>(name, support);
		lastError = new ObjectProperty<>(name + "-last", support);
	}

	@Override
	public void notifyError(final Object source, final ConversionError error) {
		errors.getValue().put(error.getProperty(), error);
		errors.fireArtificialChange(source);
		lastError.setValue(source, error);
	}

	@Override
	public void clearError(final Object source, final AbstractProperty property) {
		errors.getValue().remove(property);
		errors.fireArtificialChange(source);
		if (lastError.getValue() != null && property.equals(lastError.getValue().getProperty())) {
			lastError.setValue(source, null);
		}
	}

	public ObjectProperty<ConversionError> getLastError() {
		return lastError;
	}

	public MapProperty<AbstractProperty, ConversionError> getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return "Errors: " + errors.getObjectValue().size() + ", last=" + lastError.getValue();
	}

}
