package ch.scaille.util.persistence.handlers;

import java.io.IOException;

/**
 * Handles the encoding/decoding of data, per MimeType or extension 
 */
public interface IStorageDataHandler {

	String getDefaultMimeType();

	String getDefaultExtension();

	boolean supports(String extensionOrType);

	<T> String encode(Class<T> targetType, T value) throws IOException;

	<T> T decode(String value, Class<T> targetType) throws IOException;

}
