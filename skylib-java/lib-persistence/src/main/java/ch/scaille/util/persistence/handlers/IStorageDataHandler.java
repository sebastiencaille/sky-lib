package ch.scaille.util.persistence.handlers;

import java.io.IOException;

public interface IStorageDataHandler {

	String getDefaultMimeType();

	boolean supports(String extensionOrType);

	String getDefaultExtension();

	<T> String encode(Class<T> targetClass, T value) throws IOException;

	<T> T decode(String value, Class<T> targetType) throws IOException;

}
