package ch.scaille.util.helpers;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.stream.Stream;

public class ClassLoaderHelper {

    private static final String CP_SEPARATOR = System.getProperty("path.separator");

    private ClassLoaderHelper() {
    }

    public static String readUTF8Resource(final String resourceName) throws IOException {
        try (final InputStream in = openResourceStream(resourceName)) {
            return JavaExt.readUTF8Stream(in);
        }
    }
    
    public static InputStream openResourceStream(final String resourceName) {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final InputStream in = cl.getResourceAsStream(resourceName);
        if (in == null) {
            throw new IllegalArgumentException("No such file in classpath: " + resourceName);
        }
        return in;
    }

    public static URL[] appClassPath() {
        final String[] cp = System.getProperty("java.class.path").split(CP_SEPARATOR);
        return Arrays.stream(cp).map(LambdaExt.uncheckF(c ->
                new URL("file://" + c))).toArray(c -> new URL[c]);
    }

    public static URL[] threadClassPath() {
        return urlClassPath((URLClassLoader) Thread.currentThread().getContextClassLoader());
    }
    
	public static URL[] guessClassPath() {
		if (Thread.currentThread().getContextClassLoader() instanceof URLClassLoader) {
            return urlClassPath((URLClassLoader) Thread.currentThread().getContextClassLoader());
        }
        return appClassPath();
	}
	
	private static URL[] urlClassPath(URLClassLoader cl) {
		final URL[] urls = cl.getURLs();
		if (urls.length == 0) {
			return appClassPath();
		}
		return urls;
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

	public static URL[] cpToURLs(String classpath) {
		if (classpath == null) {
			return new URL[0];
		}
		return Arrays.stream(classpath.split(CP_SEPARATOR)).map(LambdaExt.uncheckF(c -> new URL("file:" + c))).toArray(URL[]::new);
	}
}
