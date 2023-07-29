package ch.scaille.tcwriter.persistence;

import java.io.IOException;

public record Resource(String type, String data) {

	public static final String MIMETYPE_UNDEFINED = "application/undefined";
	public static final String MIMETYPE_JSON = "application/json";
	public static final String MIMETYPE_YAML = "application/yaml";

	public interface Decoder<T> {
		T decode(Resource r) throws IOException;
	}

	public <T> T decode(Decoder<T> decoder) throws IOException {
		return decoder.decode(this);
	}

	public static Resource of(String locator, String content) {
		final var ext = extensionOf(locator);
		final String type;
		if ("json".equalsIgnoreCase(ext)) {
			type = MIMETYPE_JSON;
		} else if ("yaml".equalsIgnoreCase(ext) || "yml".equalsIgnoreCase(ext)) {
			type = MIMETYPE_YAML;
		} else {
			type = MIMETYPE_UNDEFINED;
		}
		return new Resource(type, content);
	}

	public static boolean hasExtension(String locator) {
		return locator.lastIndexOf('.') > 0;
	}
	
	public static String extensionOf(String locator) {
		var extIdx = locator.lastIndexOf('.');
		return extIdx > 0 ? locator.substring(extIdx + 1) : "";
	}
}
