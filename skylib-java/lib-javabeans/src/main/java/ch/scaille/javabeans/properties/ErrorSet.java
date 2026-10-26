package ch.scaille.javabeans.properties;

import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.IPropertiesGroup;
import lombok.Getter;

/**
 * To track the current errors (validation, exceptions, ...) 
 */
@Getter
public class ErrorSet implements ErrorNotifier {

	private final MapProperty<IProperty, ConversionError> errors;

	private final ObjectProperty<@Nullable ConversionError> lastError;

	public ErrorSet(final String name, final IPropertiesGroup support) {
		errors = new MapProperty<>(name, support);
		lastError = new ObjectProperty<>(name + "-last", support, null );
	}

	private Map<IProperty, ConversionError> errors() {
		return Objects.requireNonNull(errors.getValue());
	}

	@Override
	public void notifyError(final Object source, final ConversionError error) {
		errors().put(error.property(), error);
		errors.flushChanges(source);
		lastError.setValue(source, error);
	}

	@Override
	public void clearError(final Object source, final IProperty property) {
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
