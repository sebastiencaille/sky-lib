package ch.skymarshall.gui.mvc.properties;

import ch.skymarshall.gui.mvc.GuiError;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.AbstractProperty.ErrorNotifier;

public class ErrorSet implements ErrorNotifier {

	private final MapProperty<AbstractProperty, GuiError> errors;

	public ErrorSet(final String name, final IScopedSupport support) {
		errors = new MapProperty<>(name, support);
	}

	@Override
	public void notifyError(final Object source, final AbstractProperty property, final GuiError error) {
		errors.getValue().put(property, error);
		errors.fireArtificialChange(source);
	}

	@Override
	public void clearError(final Object source, final AbstractProperty property) {
		errors.getValue().remove(property);
		errors.fireArtificialChange(source);
	}

	public MapProperty<AbstractProperty, GuiError> getErrors() {
		return errors;
	}

}
