package ch.scaille.util.helpers;

import static ch.scaille.util.helpers.LambdaExt.uncheckedF;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * To select some classes according to an annotation, ...
 *
 * @author scaille
 */
public class ClassFinder {

	public static final Logger LOGGER = Logs.of(ClassFinder.class);

	private static final String CLASS_EXTENSION = ".class";

	public enum Policy {
		/**
		 * Gather all subclasses of the matching class
		 */
		ALL_SUBCLASSES,
		/**
		 * Gather the matching class only
		 */
		CLASS_ONLY,
		/**
		 * Internal usage
		 */
		SCANNED
	}

	public static class URLClassFinder extends ClassFinder implements AutoCloseable {

		protected URLClassFinder(URL[] uris) {
			super(new URLClassLoader(uris));
		}

		@Override
		public void close() throws IOException {
			((URLClassLoader) loader).close();
		}

	}

	private final Map<Class<?>, Policy> collectedClasses = new ConcurrentHashMap<>();

	private final Map<Class<?>, Policy> expectedAnnotation = new HashMap<>();

	private final Set<Class<?>> expectedSuperClasses = new HashSet<>();

	protected final ClassLoader loader;

	private final Function<URL, Scanner> defaultScanner = u -> new FsScanner();

	private final List<String> packagesToScan = new ArrayList<>();

	private Function<URL, Scanner> scanners = defaultScanner;

	private final List<String> libPackages = new ArrayList<>();

	public static URLClassFinder of(URL[] cp) {
		return new URLClassFinder(cp);
	}

	public static URLClassFinder source(final File... source) {
		return new URLClassFinder(
				Arrays.stream(source).map(LambdaExt.uncheckedF(f -> f.toURI().toURL())).toArray(URL[]::new));

	}

	public static ClassFinder with(Class<?> classes) {
		return new ClassFinder(Thread.currentThread().getContextClassLoader()).withClasses(classes);
	}

	public static ClassFinder ofCurrentThread() {
		return new ClassFinder(Thread.currentThread().getContextClassLoader());
	}

	protected ClassFinder(ClassLoader loader) {
		this.loader = loader;
		this.collectedClasses.put(Object.class, Policy.SCANNED);
	}

	public ClassFinder withLibPackages(Collection<String> packages) {
		libPackages.addAll(packages);
		return this;
	}
	
	public ClassFinder withClasses(Class<?>... classes) {
		collectedClasses.putAll(Arrays.stream(classes).collect(Collectors.toMap(c -> c, c -> Policy.CLASS_ONLY)));
		return this;
	}

	public ClassFinder withScanners(Function<URL, Scanner> scanner) {
		this.scanners = scanner;
		return this;
	}

	public Class<?> loadByName(final String className) {

		Class<?> found = null;
		for (final var pkg : libPackages) {
			try {
				found = loader.loadClass(pkg + className);
				break;
			} catch (final ClassNotFoundException e) {
				// ignore
			}
		}
		if (found == null) {
			throw new IllegalStateException("Not found: " + className);
		}
		return found;
	}

	public ClassFinder withAnnotation(final Class<?> tag, final Policy policy) {
		if (!tag.isAnnotation()) {
			throw new IllegalArgumentException("Class is not an annotation: " + tag.getName());
		}
		expectedAnnotation.put(tag, policy);
		return this;
	}

	public ClassFinder withSuperClass(final String className) throws ClassNotFoundException {
		expectedSuperClasses.add(Class.forName(className, true, loader));
		return this;
	}

	public ClassFinder withPackages(final String... packageName) {
		packagesToScan.addAll(List.of(packageName));
		return this;
	}

	public interface Scanner {
		Stream<Class<?>> scan(final URL resource, final String aPackage) throws IOException;
	}

	public class FsScanner implements Scanner {

		/**
		 * Gets the FileSystems of the uri
		 */
		protected URI rootOf(URI uri) {
			if (uri.getPath() != null && uri.getPath().indexOf('!') >= 0) {
				final var uriStr = uri.toString();
				return URI.create(uriStr.substring(0, uriStr.indexOf('!')));
			} else if (uri.getPath() != null) {
				return uri.resolve("/");
			} else {
				return URI.create(uri.getScheme() + ':' + rootOf(URI.create(uri.getSchemeSpecificPart())));
			}
		}

		/**
		 * Gets the location of a package 
		 */
		protected String packageLocationOf(URI uri, String aPackage) {
			if (uri.getPath() == null) {
				return "/";
			}
			final var uriPath = JavaExt.pathOf(uri);
			return uriPath.substring(0, uriPath.length() - aPackage.length() - 1);
		}

		@Override
		public Stream<Class<?>> scan(URL resource, String aPackage) throws IOException {
			if (resource == null) {
				return Stream.empty();
			}
			final URI rootUri;
			final String scanPath;
			try {
				final var uri = resource.toURI();
				rootUri = rootOf(uri);
				scanPath = packageLocationOf(uri, aPackage);
			} catch (URISyntaxException e) {
				throw new IOException("Unable to scan files", e);
			}
			try (var fs = FileSystems.newFileSystem(rootUri, Collections.emptyMap())) {
				return scan(fs.getPath(scanPath), aPackage);
			} catch (FileSystemAlreadyExistsException e) {
				return scan(FileSystems.getFileSystem(rootUri).getPath(scanPath), aPackage);
			}
		}

		private Stream<Class<?>> scan(final Path rootOfPackage, String aPackage) throws IOException {
			return StreamExt.onCloseableF(Files.walk(rootOfPackage.resolve(aPackage.replace('.', '/'))),
				// We need a terminal operation before the close. Also, the toCollection is needed for cast reasons
				walk -> walk.map(rootOfPackage::relativize)
						.map(Path::toString)
						.filter(p -> p.endsWith(CLASS_EXTENSION))
						.map(p -> p.replace(CLASS_EXTENSION, "").replace('/', '.').replace("\\", "."))
						.map(ClassFinder.this::handleClass)
						.collect(Collectors.toCollection((Supplier<HashSet<Class<?>>>) HashSet::new)))
					.stream();
		}

	}

	private Class<?> handleClass(final String className) {
		if ("module-info".equals(className)) {
			return null;
		}
		try {
			final var candidate = Class.forName(className, false, this.loader);
			final var result = processClass(candidate);
			if (result != Policy.SCANNED) {
				return candidate;
			}
		} catch (final Exception | NoClassDefFoundError e) { // NOSONAR
			// ignore
			LOGGER.log(Level.FINE, "Unable to load class", e);
		}
		return null;
	}

	private Policy matchesExpectations(final Class<?> clazz) {
		if (collectedClasses.containsKey(clazz)) {
			return collectedClasses.get(clazz);
		}
		if (expectedSuperClasses.contains(clazz)) {
			return Policy.ALL_SUBCLASSES;
		}
		for (final var annotation : clazz.getAnnotations()) {
			final var policy = expectedAnnotation.get(annotation.annotationType());
			if (policy != null) {
				return policy;
			}
		}
		return Policy.SCANNED;
	}

	private Policy processClass(final Class<?> clazz) {
		if (collectedClasses.containsKey(clazz)) {
			// already processed
			return collectedClasses.get(clazz);
		}
		var appliedPolicy = matchesExpectations(clazz);
		if (appliedPolicy == Policy.SCANNED && !Object.class.equals(clazz.getSuperclass())) {
			appliedPolicy = scanInheritedClass(clazz.getSuperclass());
		}
		if (appliedPolicy == Policy.SCANNED) {
			for (final var iface : clazz.getInterfaces()) {
				appliedPolicy = scanInheritedClass(iface);
				if (appliedPolicy != Policy.SCANNED) {
					break;
				}
			}
		}
		if (expectedAnnotation.isEmpty() && expectedSuperClasses.isEmpty()
				&& packagesToScan.stream().anyMatch(p -> clazz.getName().startsWith(p))) {
			appliedPolicy = Policy.CLASS_ONLY;
		}
		collectedClasses.put(clazz, appliedPolicy);
		return appliedPolicy;
	}

	private Policy scanInheritedClass(final Class<?> inheritedClass) {
		if (inheritedClass == null) {
			return Policy.SCANNED;
		}
		var appliedPolicy = processClass(inheritedClass);
		if (appliedPolicy == Policy.CLASS_ONLY) {
			// parent class policy is CLASS_ONLY, skip
			appliedPolicy = Policy.SCANNED;
		}
		return appliedPolicy;
	}

	private Stream<Class<?>> scan(String aPackage) throws IOException {
		return Collections.list(loader.getResources(aPackage))
				.stream()
				.flatMap(uncheckedF(r -> scanners.apply(r).scan(r, aPackage)));
	}

	public Stream<Class<?>> scan() {
		return packagesToScan.stream().map(p -> p.replace(".", "/")). //
				flatMap(uncheckedF(this::scan)). //
				filter(Objects::nonNull).distinct();
	}

}
