package ch.skymarshall.gui.mvc.properties;

import ch.skymarshall.gui.mvc.GuiErrors.GuiError;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.AbstractProperty.ErrorNotifier;

public class ErrorSet implements ErrorNotifier {

	private final MapProperty<Object, GuiError> errors;

	public ErrorSet(final String name, final IScopedSupport support) {
		errors = new MapProperty<>(name, support);
	}

	@Override
	public void notifyError(final Object source, final GuiError error) {
		errors.getValue().put(source, error);
		errors.fireArtificialChange(source);
	}

	@Override
	public void clearError(final Object source) {
		errors.getValue().remove(source);
		errors.fireArtificialChange(source);
	}

	public MapProperty<Object, GuiError> getErrors() {
		return errors;
	}

}
