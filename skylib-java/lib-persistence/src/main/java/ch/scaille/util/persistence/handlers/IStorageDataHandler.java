package ch.scaille.util.persistence.handlers;

import java.io.IOException;

/**
 * Handles the encoding/decoding of data, per MimeType or extension 
 */
public interface IStorageDataHandler {

	String getDefaultMimeType();

	String getDefaultExtension();

	boolean supports(String extensionOrType);

	/**
	 * TODO Use streams
	 */
	<T> String encode(Class<T> targetType, T value) throws IOException;

	/**
	 * TODO Use streams
	 */
	default <T> T decode(String value, Class<T> targetType) throws IOException {
		return decode(value, targetType, null);
	}

	<T> T decode(String value, Class<T> targetType, T template) throws IOException;
}
