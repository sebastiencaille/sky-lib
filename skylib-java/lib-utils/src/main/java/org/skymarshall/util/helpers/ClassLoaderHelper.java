package org.skymarshall.util.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ClassLoaderHelper {

	private ClassLoaderHelper() {

	}

	public static InputStream openResourceStream(final String resourceName) {
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		final InputStream in = cl.getResourceAsStream(resourceName);
		if (in == null) {
			throw new IllegalArgumentException("No such file in classpath: " + resourceName);
		}
		return in;
	}

	public static String readResource(final InputStream in) throws IOException {
		final StringBuilder result = new StringBuilder();

		final InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
		final char[] buffer = new char[1024 * 1024];
		int read;
		while ((read = inReader.read(buffer)) > 0) {
			result.append(buffer, 0, read);
		}
		return result.toString();
	}

	public static String readUTF8Resource(final String resourceName) throws IOException {
		try (final InputStream in = openResourceStream(resourceName)) {
			return readResource(in);
		}
	}
}
