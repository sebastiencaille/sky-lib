package ch.scaille.util.persistence.handlers;

import java.io.IOException;

public class TextStorageHandler implements IStorageDataHandler {

	public static final String TEXT_MIMETYPE = "application/text";
	
	@Override
	public String getDefaultMimeType() {
		return TEXT_MIMETYPE;
	}

	@Override
	public boolean supports(String extensionOrType) {
		return TEXT_MIMETYPE.equals(extensionOrType);
	}

	@Override
	public String getDefaultExtension() {
		return null;
	}

	@Override
	public <T> String encode(Class<T> targetType, T value) throws IOException {
		if (!String.class.equals(targetType)) {
			throw new IllegalStateException("Expected String target, got " + targetType);
		}
		return value.toString();
	}

	@Override
	public <T> T decode(String value, Class<T> targetType) throws IOException {
		if (!String.class.equals(targetType)) {
			throw new IllegalStateException("Expected String target, got " + targetType);
		}
		return (T)value;
	}

}
