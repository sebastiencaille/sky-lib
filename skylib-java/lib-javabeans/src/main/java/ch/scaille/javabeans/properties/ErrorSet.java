package ch.scaille.javabeans.properties;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * To track the current errors (validation, exceptions, ...) 
 */
@Getter
@NullMarked
public class ErrorSet implements ErrorNotifier {

	private final MapProperty<AbstractProperty, ConversionError> errors;

	private final ObjectProperty<@Nullable ConversionError> lastError;

	public ErrorSet(final String name, final IPropertiesGroup support) {
		errors = new MapProperty<>(name, support);
		lastError = new ObjectProperty<>(name + "-last", support, null );
	}

	private Map<AbstractProperty, ConversionError> errors() {
		return errors.getValue();
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

    @Override
	public String toString() {
		return "Errors: " + errors().size() + ", last=" + lastError.getValue();
	}

}
