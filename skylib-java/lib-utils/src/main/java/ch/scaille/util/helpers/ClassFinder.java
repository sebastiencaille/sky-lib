/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.scaille.util.helpers;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * To select some classes according to an annotation, ...
 *
 * @author scaille
 *
 */
public class ClassFinder {

	private static final String[] DEFAULT_PACKAGES = { "", "ch.scaille.gui.mvc.properties.",
			"ch.scaille.gui.mvc.persisters.", "ch.scaille.gui." };

	public enum Policy {
		/**
		 * Gather all subclasses of the matching class
		 */
		ALL_SUBCLASSES,
		/**
		 * Gather the matching class only
		 */
		CLASS_ONLY
	}

	private static final String CLASS_EXTENSION = ".class";

	private final Map<Class<?>, Policy> collectedClasses = new HashMap<>();

	private final Map<Class<?>, Policy> expectedTags = new HashMap<>();

	private final Set<Class<?>> expectedSuperClasses = new HashSet<>();

	private final URLClassLoader loader;

	private final Function<URL, Scanner> defaultScanner = u -> new FsScanner();

	private Function<URL, Scanner> scanners = defaultScanner;

	public static ClassFinder forApp() {
		return new ClassFinder(ClassLoaderHelper.appClassPath());
	}

	public static ClassFinder source(final File... source) {

		return new ClassFinder(Arrays.stream(source).map(f -> {
			try {
				return f.toURI().toURL();
			} catch (final MalformedURLException e) {
				throw new IllegalStateException("Unable process folder " + source, e);
			}
		}).collect(Collectors.toList()).toArray(new URL[0]));

	}

	public ClassFinder(final URL[] urls) {
		this.loader = new URLClassLoader(urls);
		this.collectedClasses.put(Object.class, null);
	}

	public void setScanners(Function<URL, Scanner> scanner) {
		this.scanners = scanner;
	}

	public Class<?> loadByName(final String className) {

		Class<?> found = null;
		for (final String pkg : DEFAULT_PACKAGES) {
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
		expectedTags.put(tag, policy);
		return this;
	}

	public ClassFinder withSuperClass(final String className) throws ClassNotFoundException {
		expectedSuperClasses.add(Class.forName(className, true, loader));
		return this;
	}

	public interface Scanner {
		void scan(final URL resource, final String aPackage) throws IOException;
	}

	private static URI root(URI uri) {
		if (uri.getPath() != null && uri.getPath().indexOf('!') >= 0) {
			String uriStr = uri.toString();
			return URI.create(uriStr.substring(0, uriStr.indexOf('!')));
		} else if (uri.getPath() != null) {
			return uri.resolve("/");
		} else {
			return URI.create(uri.getScheme() + ':' + root(URI.create(uri.getSchemeSpecificPart())));
		}
	}

	private static String base(URI uri, String aPackage) {
		if (uri.getPath() == null) {
			return "/";
		}
		String uriPath = uri.getPath();
		return uriPath.substring(0, uriPath.length() - aPackage.length());
	}

	public class FsScanner implements Scanner {

		@Override
		public void scan(URL resource, String aPackage) throws IOException {
			if (resource == null) {
				return;
			}
			URI rootUri;
			String scanPath;
			try {
				URI uri = resource.toURI();
				rootUri = root(uri);
				scanPath = base(uri, aPackage)  + '/' + aPackage.replace('.', '/');
			} catch (URISyntaxException e) {
				throw new IOException("Unable to scan files", e);
			}
			try (FileSystem fs = FileSystems.newFileSystem(rootUri, Collections.emptyMap())) {
				scan(fs.getPath(scanPath ), aPackage);
			} catch (FileSystemAlreadyExistsException e) {
				scan(FileSystems.getFileSystem(rootUri).getPath(scanPath), aPackage);
			}
		}

		private void scan(final Path path, String aPackage) throws IOException {
			try (Stream<Path> walk = Files.walk(path)) {
				walk.map(Path::toString).filter(p -> p.endsWith(CLASS_EXTENSION))
						.forEach(p -> handleClass(p.replace(CLASS_EXTENSION, "").substring(1).replace('/', '.')));
			}
		}

	}

	public ClassFinder collect(final String basePackage) throws IOException {
		final Enumeration<URL> packages = loader.getResources(basePackage.replace(".", "/"));
		while (packages.hasMoreElements()) {
			URL resource = packages.nextElement();
			scanners.apply(resource).scan(resource, basePackage);
		}
		return this;
	}

	private void handleClass(final String className) {
		if ("module-info".equals(className)) {
			return;
		}
		try {
			processClass(Class.forName(className, false, this.loader));
		} catch (final Exception | NoClassDefFoundError e) { // NOSONAR
			// ignore
		}
	}

	private Policy match(final Class<?> clazz) {
		if (collectedClasses.containsKey(clazz)) {
			return collectedClasses.get(clazz);
		}
		if (expectedSuperClasses.contains(clazz)) {
			return Policy.ALL_SUBCLASSES;
		}
		for (final Annotation annotation : clazz.getAnnotations()) {
			final Policy policy = expectedTags.get(annotation.annotationType());
			if (policy != null) {
				return policy;
			}
		}
		return null;
	}

	private Policy processClass(final Class<?> clazz) {
		if (collectedClasses.containsKey(clazz)) {
			// already processed
			return collectedClasses.get(clazz);
		}
		Policy appliedPolicy = match(clazz);
		if (appliedPolicy == null && clazz.getSuperclass() != null) {
			appliedPolicy = processClass(clazz.getSuperclass());
			if (appliedPolicy == Policy.CLASS_ONLY) {
				// parent class policy is CLASS_ONLY, skip
				appliedPolicy = null;
			}
		}
		if (appliedPolicy == null) {
			for (final Class<?> iface : clazz.getInterfaces()) {
				appliedPolicy = processClass(iface);
				if (appliedPolicy == Policy.CLASS_ONLY) {
					appliedPolicy = null;
				}
				if (appliedPolicy != null) {
					break;
				}
			}
		}
		collectedClasses.put(clazz, appliedPolicy);
		return appliedPolicy;
	}

	public Set<Class<?>> getResult() {
		return collectedClasses.entrySet().stream().filter(e -> e.getValue() != null).map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}

}
