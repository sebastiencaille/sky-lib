package ch.scaille.javabeans.properties;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Objects;

/**
 * To track the current errors (validation, exceptions, ...) 
 */
@NullMarked
public class ErrorSet implements ErrorNotifier {

	private final MapProperty<AbstractProperty, ConversionError> errors;

	private final ObjectProperty<ConversionError> lastError;

	public ErrorSet(final String name, final IPropertiesGroup support) {
		errors = new MapProperty<>(name, support);
		lastError = new ObjectProperty<>(name + "-last", support);
	}

	private Map<AbstractProperty, ConversionError> errors() {
		return Objects.requireNonNull(errors.getValue());
	}

	@Override
	public void notifyError(final Object source, final ConversionError error) {
		errors().put(error.property(), error);
		errors.flushChanges(source);
		lastError.setValue(source, error);
	}

	@Override
	public void clearError(final Object source, final AbstractProperty property) {
		errors().remove(property);
		errors.flushChanges(source);
		if (lastError.getValue() != null && property.equals(lastError.getValue().property())) {
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
		return "Errors: " + errors().size() + ", last=" + lastError.getValue();
	}

}
