package ch.scaille.util.helpers;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassLoaderHelper {

	private static final String CP_SEPARATOR = System.getProperty("path.separator");

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

	public static String readUTF8Resource(final InputStream in) throws IOException {
		final StringBuilder result = new StringBuilder();

		final InputStreamReader inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
		final char[] buffer = new char[1024 * 1024];
		int read;
		while ((read = inReader.read(buffer)) > 0) {
			result.append(buffer, 0, read);
		}
		return result.toString();
	}

	public static String readUTF8Resource(final String resourceName) throws IOException {
		try (final InputStream in = openResourceStream(resourceName)) {
			return readUTF8Resource(in);
		}
	}

	public static URL[] appClassPath() {
		final String[] cp = System.getProperty("java.class.path").split(CP_SEPARATOR);
		return Arrays.stream(cp).map(LambdaExt.uncheckF(c -> 
				new URL("file://" + c))).collect(Collectors.toList()).toArray(new URL[0]);
	}
	
	public static URL[] threadClassPath() {
		return ((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs();
	}

	public static URL[] guessClassPath() {
		if (Thread.currentThread().getContextClassLoader() instanceof URLClassLoader) {
			return threadClassPath();
		}
		return appClassPath();
	}
	
	public static String cpToCommandLine(final URL[] classpath, final URL... extra) {
		return Stream.concat(Stream.of(classpath), Stream.of(extra)).map(URL::getFile).collect(joining(CP_SEPARATOR));
	}

	public static void registerResourceHandler() {
		String handlerPkg = "ch.scaille.util.helpers.protocols";
		String currentProp = System.getProperty("java.protocol.handler.pkgs");
		if (currentProp == null || !currentProp.contains(handlerPkg)) {
			if (currentProp != null) {
				handlerPkg = handlerPkg + '|' + currentProp;
			}
			System.setProperty("java.protocol.handler.pkgs", handlerPkg);
		}
	}
}
