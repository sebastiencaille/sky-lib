package ch.skymarshall.gui.mvc;

import ch.skymarshall.gui.mvc.properties.AbstractProperty;

/**
 * Error container
 *
 * @author Sebastien Caille
 *
 */
public class GuiError {
	private final AbstractProperty property;
	private final Object content;
	private final String message;

	public GuiError(final AbstractProperty property, final String message, final Object content) {
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