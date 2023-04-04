package ch.scaille.gui.mvc.properties;

import ch.scaille.gui.mvc.GuiError;
import ch.scaille.gui.mvc.IPropertiesGroup;
import ch.scaille.gui.mvc.properties.AbstractProperty.ErrorNotifier;

public class ErrorSet implements ErrorNotifier {

	private final MapProperty<AbstractProperty, GuiError> errors;

	private final ObjectProperty<GuiError> lastError;

	public ErrorSet(final String name, final IPropertiesGroup support) {
		errors = new MapProperty<>(name, support);
		lastError = new ObjectProperty<>(name + "-last", support);
	}

	@Override
	public void notifyError(final Object source, final GuiError error) {
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

	public ObjectProperty<GuiError> getLastError() {
		return lastError;
	}

	public MapProperty<AbstractProperty, GuiError> getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return "Errors: " + errors.getObjectValue().size() + ", last=" + lastError.getValue();
	}

}
