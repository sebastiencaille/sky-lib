package ch.scaille.util.helpers;

import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class ClassLoaderHelper {

	private static final String CP_SEPARATOR = File.pathSeparator;
	private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");

	private ClassLoaderHelper() {
	}

	public static Stream<URL> clean(Stream<URL> original) {
		return original.distinct().filter(u -> !u.toString().endsWith(".idx"));
	}

	public static String readUTF8Resource(final String resourceName) throws IOException {
		try (final var inStream = openResourceStream(resourceName)) {
			return JavaExt.readUTF8Stream(inStream);
		}
	}

	public static InputStream openResourceStream(final String resourceName) {
		final var cl = Thread.currentThread().getContextClassLoader();
		final var inStream = cl.getResourceAsStream(resourceName);
		if (inStream == null) {
			throw new IllegalArgumentException("No such file in classpath: " + resourceName);
		}
		return inStream;
	}

	public static URL[] appClassPath() {
		final var cp = System.getProperty("java.class.path").split(CP_SEPARATOR);
		return clean(Arrays.stream(cp)
				.map(LambdaExt.uncheckedF(jar -> Paths.get(jar).toUri().toURL())))
				.toArray(URL[]::new);
	}

	public static URL[] threadClassPath() {
		return urlClassPath((URLClassLoader) Thread.currentThread().getContextClassLoader());
	}

	public static URL[] guessClassPath() {
		if (Thread.currentThread().getContextClassLoader() instanceof URLClassLoader urlClassLoader) {
			return urlClassPath(urlClassLoader);
		}
		return appClassPath();
	}

	private static URL[] urlClassPath(URLClassLoader cl) {
		final var urls = cl.getURLs();
		if (urls.length == 0) {
			return appClassPath();
		}
		return clean(Arrays.stream(urls)).toArray(URL[]::new);
	}

	public static String cpToCommandLine(final URL[] classpath, final URL... extra) {
		return Stream.concat(Stream.of(classpath), Stream.of(extra))
				.map(URL::getFile)
				.map(c -> IS_WINDOWS ? c.substring(1).replace('/', '\\') : c)
				.collect(joining(CP_SEPARATOR));
	}

	public static void registerResourceHandler() {
		var handlerPkg = "ch.scaille.util.helpers.protocols";
		final var currentProp = System.getProperty("java.protocol.handler.pkgs");
		if (currentProp == null || !currentProp.contains(handlerPkg)) {
			if (currentProp != null) {
				handlerPkg = handlerPkg + '|' + currentProp;
			}
			System.setProperty("java.protocol.handler.pkgs", handlerPkg);
		}
	}

	public static URL[] cpToURLs(String classpath) {
		if (classpath == null) {
			return new URL[0];
		}
		return Arrays.stream(classpath.split(CP_SEPARATOR))
				.map(LambdaExt.uncheckedF(c -> Paths.get(c).toUri().toURL()))
				.toArray(URL[]::new);
	}
}
