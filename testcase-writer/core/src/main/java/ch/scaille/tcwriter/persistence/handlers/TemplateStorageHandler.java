package ch.scaille.tcwriter.persistence.handlers;

import ch.scaille.util.persistence.handlers.IStorageDataHandler;
import ch.scaille.util.persistence.handlers.TextStorageHandler;

public class TemplateStorageHandler extends TextStorageHandler implements IStorageDataHandler {

	public static final String TEMPLATE_MIMETYPE = "application/template";
	
	@Override
	public String getDefaultMimeType() {
		return TEMPLATE_MIMETYPE;
	}

	@Override
	public boolean supports(String extensionOrType) {
		return TEMPLATE_MIMETYPE.equals(extensionOrType) || getDefaultExtension().equals(extensionOrType);
	}

	@Override
	public String getDefaultExtension() {
		return "template";
	}

	@Override
	public <T> String encode(Class<T> targetType, T value) {
		if (!String.class.equals(targetType)) {
			throw new IllegalStateException("Expected String target, got " + targetType);
		}
		return value.toString();
	}

	@Override
	public <T> T decode(String value, Class<T> targetType) {
		if (!String.class.equals(targetType)) {
			throw new IllegalStateException("Expected String target, got " + targetType);
		}
		return (T)value;
	}

}
