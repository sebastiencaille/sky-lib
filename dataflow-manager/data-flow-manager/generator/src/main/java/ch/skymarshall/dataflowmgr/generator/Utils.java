package ch.skymarshall.dataflowmgr.generator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Utils {
	public static final Charset UTF8 = Charset.forName("UTF-8");

	public static String toCamelCase(final String str) {
		final StringBuilder result = new StringBuilder();

		final String[] parts = str.split("_");
		for (final String part : parts) {
			result.append(Character.toUpperCase(part.charAt(0)));
			if (part.length() > 1) {
				result.append(part.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}

	public static String firstUpperCase(final String str) {
		if (str.length() == 1) {
			return str.toUpperCase();
		}
		final char firstUpper = Character.toUpperCase(str.charAt(0));
		return firstUpper + str.substring(1);
	}

	public static String firstLowerCase(final String str) {
		if (str.length() == 1) {
			return str.toLowerCase();
		}
		final char firstUpper = Character.toLowerCase(str.charAt(0));
		return firstUpper + str.substring(1);
	}

	public static String read(final InputStream in) throws IOException {
		final StringBuilder result = new StringBuilder();

		final InputStreamReader inReader = new InputStreamReader(in, UTF8);
		final char[] buffer = new char[1024 * 1024];
		int read;
		while ((read = inReader.read(buffer)) > 0) {
			result.append(buffer, 0, read);
		}
		return result.toString();
	}

}
